package com.running.runapp.domain.groupRunning.service;

import com.running.runapp.domain.groupRunning.domain.GroupMember;
import com.running.runapp.domain.groupRunning.domain.GroupRole;
import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import com.running.runapp.domain.groupRunning.domain.GroupStatus;
import com.running.runapp.domain.groupRunning.dto.GroupRequest;
import com.running.runapp.domain.groupRunning.dto.GroupResponse;
import com.running.runapp.domain.groupRunning.repository.GroupMemberRepository;
import com.running.runapp.domain.groupRunning.repository.GroupRunningRepository;
import com.running.runapp.domain.member.domain.Member;
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

        // ########### [조건1] 해당 그룹이 모집중인지 확인 ###########
        if (groupRunning.getStatus() != GroupStatus.RECRUITING) {
            throw new BusinessException(ErrorCode.NOT_RECRUITING);
        }

        // ########### [조건2] 다른 그룹런의 시간과 겹치는지 확인 ###########
        if (groupMemberRepository.hasOverlappingSchedule(member, groupRunning.getStartTime(), groupRunning.getEndTime())) {
            throw new BusinessException(ErrorCode.DUPLICATE_GROUP_TIME);
        }

        // ########### [조건3] 이미 이 그룹에 들어가 있는지 확인 ###########
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
     * 그룹 러닝 시작
     */
    @Transactional
    public GroupResponse.GroupDetail groupRunStart(Long groupId, Member member) {
        // 그룹 유무 확인
        GroupRunning groupRunning = getGroupRunning(groupId);

        // host와 member가 같은지 검사
        groupRunning.verify(member);

        // [조건1] 그룹 러닝이 이미 끝났을 때
        if (groupRunning.isAlreadyEnded()) {
            throw new BusinessException(ErrorCode.ALREADY_END_RUNNING);
        }

        // [조건2] 그룹 러닝 아직 시작 시간이 아닐 때
        if (!groupRunning.isAlreadyStarted()) {
            throw new BusinessException(ErrorCode.NOT_START_TIME_YET);
        }

        return GroupResponse.GroupDetail.from(groupRunning, member.getId());
    }

    /**
     * 그룹 러닝 종료
     */
    public GroupResponse.GroupDetail groupRunFinish(Long groupId, Member member) {
        // 그룹 유무 확인
        GroupRunning groupRunning = getGroupRunning(groupId);

        // host와 member가 같은지 검사
        groupRunning.verify(member);

        // 상태 검증 (이미 종료되었거나, 아직 시작 안 한 방은 종료할 수 없음)
        if (groupRunning.getDynamicStatus() == GroupStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.ALREADY_END_RUNNING);
        }
        if (groupRunning.getDynamicStatus() == GroupStatus.RECRUITING) {
            throw new BusinessException(ErrorCode.NOT_START_TIME_YET);
        }

        // 그룹 러닝 상태 종료로 변경
        groupRunning.groupRunEnd();

        // 나중에 추가할 부분] 웹소켓으로 종료 이벤트 발행
        // messagingTemplate.convertAndSend("/topic/group/" + groupId, new RunEndEvent());

        // [나중에 추가할 부분] Redis 메모리 정리
        // 실시간 위치 추적을 위해 Redis에 쌓아두었던 이 방의 데이터를 삭제해서 메모리 확보
        // redisTemplate.delete("group_location:" + groupId);

        return GroupResponse.GroupDetail.from(groupRunning, member.getId());
    }


    /**
     * 그룹 목록 조회
     */
    public Slice<GroupResponse.GroupSummary> findAllGroups(Pageable pageable, Long memberId) {
        return groupRunningRepository.findAllByFilter(pageable, memberId);
    }


    // 단일 그룹러닝 모집 상세 조회
    public GroupResponse.GroupDetail getGroupDetailInfo(Long groupId, Long memberId) {
        GroupRunning groupRunning = groupRunningRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        return GroupResponse.GroupDetail.from(groupRunning, memberId);
    }
}
