package com.running.runapp.domain.profile.service;

import com.running.runapp.domain.profile.domain.Follow;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.dto.SocialResponse;
import com.running.runapp.domain.profile.repository.FollowRepository;
import com.running.runapp.domain.running.domain.RunningRecord;
import com.running.runapp.domain.running.repository.RunningRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FollowRepository followRepository;
    private final RunningRecordRepository runningRecordRepository;

    // 친구 활동 피드 조회
    public List<SocialResponse.FeedItem> friendFeed(Member me) {
        // 1. 친구상태인 유저 ID 리스트 조회
        List<Long> friendIds = followRepository.findFriendIds(me.getId(), Follow.FollowStatus.ACCEPTED);

        if (friendIds == null || friendIds.isEmpty()) {
            return List.of();
        }

        // 2. 친구들의 최근 러닝 기록 TOP 30 조회
        List<RunningRecord> records =
                runningRecordRepository.findTop30ByMember_IdInOrderByStartTimeDesc(friendIds);

        // 3. 응답 DTO 변환
        return records.stream()
                .map(r -> SocialResponse.FeedItem.builder()
                        .type("RUN")
                        .memberId(r.getMember().getId())
                        .runId(r.getId())
                        .startTime(r.getStartTime())
                        .totalDistance(r.getTotalDistance())
                        .build()
                )
                .toList();
    }
}