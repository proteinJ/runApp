package com.running.runapp.domain.groupRunning.service;

import com.running.runapp.domain.groupRunning.domain.GroupMember;
import com.running.runapp.domain.groupRunning.domain.GroupRole;
import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import com.running.runapp.domain.groupRunning.dto.GroupRequest;
import com.running.runapp.domain.groupRunning.dto.GroupResponse;
import com.running.runapp.domain.groupRunning.repository.GroupMemberRepository;
import com.running.runapp.domain.groupRunning.repository.GroupRunningRepository;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {

    private final GroupRunningRepository groupRunningRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;

    private GroupRunning getGroupRunning(Long groupId) {
        return groupRunningRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
    }


    @Transactional
    public Long groupAdd(GroupRequest.groupAdd dto, Member member) {

        GroupRunning groupRunning = groupRunningRepository.save(dto.toEntity(member));

        GroupMember host = GroupMember.builder()
                .groupRunning(groupRunning)
                .member(member)
                .role(GroupRole.HOST)
                .build();

        groupMemberRepository.save(host); // DB 저장 담당
        groupRunning.addParticipants(member); // 메모리 객체 상태 업데이트 담당

        return groupRunning.getId();
    }

    @Transactional
    public Long groupEdit(GroupRequest.UpdateExtraRequest dto, Long groupId, Member member) {

        // 그룹 유무 확인
        GroupRunning groupRunning = getGroupRunning(groupId);

        // host와 member가 같은지 검사
        groupRunning.verify(member);

        // update Function (Domain안의 함수)
        groupRunning.updateInfo(dto);

        return groupRunning.getId();
    }

    @Transactional
    public void groupDelete(Long groupId, Member member) {

        // 그룹 유무 확인
        GroupRunning groupRunning = getGroupRunning(groupId);

        // host와 member가 같은지 검사
        groupRunning.verify(member);

        groupRunningRepository.delete(groupRunning);
        groupMemberRepository.bulkSoftDeleteByGroup(groupId);
    }


    @Transactional
    public void groupJoin(Long groupId, Member member) {

        // 그룹 유무 확인
        GroupRunning groupRunning = getGroupRunning(groupId);

        // ########### [조건1] 다른 그룹런의 시간과 겹치는지 확인 ###########
        if (groupMemberRepository.hasOverlappingSchedule(member, groupRunning.getStartTime(), groupRunning.getEndTime())) {
            throw new BusinessException(ErrorCode.DUPLICATE_GROUP_TIME);
        }

        // ########### [조건2] 이미 이 그룹에 들어가 있는지 확인 ###########
        if (groupMemberRepository.existsByGroupRunningIdAndMember(groupRunning.getId(), member)) {
            throw new BusinessException(ErrorCode.ALREADY_JOINED_GROUP);
        }

        GroupMember participant = GroupMember.builder()
                .groupRunning(groupRunning)
                .member(member)
                .role(GroupRole.PARTICIPANT)
                .build();

        groupMemberRepository.save(participant);
        groupRunning.addParticipants(member);
    }

    @Transactional
    public void groupLeave(Long groupId, Member member) {

        GroupMember groupMember = groupMemberRepository.findByGroupRunningIdAndMember(groupId, member)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_PARTICIPATED));

        if (groupMember.getGroupRunning().isAlreadyEnded()) {
            throw new BusinessException(ErrorCode.ALREADY_END_RUNNING);
        } else if (groupMember.getGroupRunning().isAlreadyStarted()) {
            throw new BusinessException(ErrorCode.ALREADY_START_RUNNING);
        }

        groupMemberRepository.delete(groupMember);
    }


    /**
     * 그룹 목록 조회
     */
    public Slice<GroupResponse.GroupSummary> findAllGroups(Pageable pageable) {
        return groupRunningRepository.findAllByFilter(pageable);
    }
}
