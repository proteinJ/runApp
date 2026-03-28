package com.running.runapp.domain.groupRunning.service;

import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import com.running.runapp.domain.groupRunning.domain.GroupStatus;
import com.running.runapp.domain.groupRunning.dto.GroupRequest;
import com.running.runapp.domain.groupRunning.repository.GroupRunningRepository;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {

    private final GroupRunningRepository groupRunningRepository;
    private final MemberRepository memberRepository;

    private GroupRunning getGroupRunning(Long groupId) {
        return groupRunningRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
    }


    @Transactional
    public Long groupAdd(GroupRequest.groupAdd dto, Member member) {

        GroupRunning groupRunning = GroupRunning.builder()
                .title(dto.title())
                .content(dto.content())
                .maxParticipants(dto.maxParticipants())
                .startTime(dto.startTime())
                .status(GroupStatus.RECRUITING)
                .host(member) // 객체 타입 안맞음 변경 필요
                .build();

        // Host 사용자도 참가인원으로 추가
        groupRunning.addParticipants(member);

        return groupRunningRepository.save(groupRunning).getId();
    }


    @Transactional
    public void groupEdit(GroupRequest.UpdateExtraRequest dto, Long groupId, Member member) {

        // 그룹 유무 확인
        GroupRunning groupRunning = getGroupRunning(groupId);

        // host와 member가 같은지 검사
        groupRunning.verify(member);

        // update Function (Domain안의 함수)
        groupRunning.updateInfo(dto.title(), dto.content(), dto.maxParticipants(), dto.startTime());
    }

    @Transactional
    public void groupDelete(Long groupId, Member member) {

        // 그룹 유무 확인
        GroupRunning groupRunning = getGroupRunning(groupId);

        // host와 member가 같은지 검사
        groupRunning.verify(member);

        groupRunningRepository.delete(groupRunning);

        groupRunning.setStatus(GroupStatus.CANCELLED);
    }


    @Transactional
    public void groupJoin(Long groupId, Member member) {

        // 그룹 유무 확인
        GroupRunning groupRunning = getGroupRunning(groupId);

        // ########### [조건] 다른 그룹런의 시간과 겹치는지 확인 ###########

        groupRunning.addParticipants(member);
    }
}
