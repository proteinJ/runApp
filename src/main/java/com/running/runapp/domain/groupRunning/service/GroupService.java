package com.running.runapp.domain.groupRunning.service;

import com.running.runapp.domain.groupRunning.domain.GroupMember;
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


    @Transactional
    public Long groupAdd(GroupRequest.groupAdd dto, Long memberId) {

        Member host = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        GroupRunning groupRunning = GroupRunning.builder()
                .title(dto.title())
                .content(dto.content())
                .maxParticipants(dto.maxParticipants())
                .startTime(dto.startTime())
                .status(GroupStatus.RECRUITING)
                .host(host) // 객체 타입 안맞음 변경 필요
                .build();

        // Host 사용자를 GroupMember 객체로 하나 만들기
        GroupMember hostParticipant = GroupMember.builder()
                .groupRunning(groupRunning)
                .member(host)
                .build();

        // Host 사용자도 참가인원으로 추가
        groupRunning.addParticipants(hostParticipant);

        return groupRunningRepository.save(groupRunning).getId();
    }


    @Transactional
    public void groupEdit(GroupRequest.UpdateExtraRequest dto, Long groupId) {

        GroupRunning groupRunning = groupRunningRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));


    }

}
