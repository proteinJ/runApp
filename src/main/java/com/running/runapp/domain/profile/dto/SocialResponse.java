package com.running.runapp.domain.profile.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class SocialResponse {

    // 닉네임 검색 결과용
    public record SearchMemberSummary(
            Long memberId,
            String nickname,
            Integer level,
            Double totalDistance,
            Double avgPace,
            String equippedTitleName,
            String relationStatus // NONE, PENDING_SENT, PENDING_RECEIVED, FRIEND
    ) {}

    @Builder
    public record FollowInfo(
            String followId,
            Long followerId,
            String followerNickname,
            Long followingId,
            String followingNickname,
            String status,
            LocalDateTime createdAt
    ) {}

    @Builder
    public record MemberSummary(
            Long memberId,
            String nickname
    ) {}

    @Builder
    public record FeedItem(
            String type,
            Long runId,
            Long memberId,
            String nickname,
            Double totalDistance,
            LocalDateTime startTime
    ) {}

    @Builder
    public record FeedResult(
            List<FeedItem> items
    ) {}
}