package com.running.runapp.domain.profile.dto;

    import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class SocialResponse {

    @Schema(description = "닉네임 검색 결과")
    public record SearchMemberSummary(
            @Schema(description = "회원 ID", example = "1")
            Long memberId,

            @Schema(description = "닉네임", example = "달리기왕")
            String nickname,

            @Schema(description = "레벨", example = "12")
            Integer level,

            @Schema(description = "총 달린 거리 (km)", example = "124.8")
            Double totalDistance,

            @Schema(description = "평균 페이스 (분/km)", example = "5.42")
            Double avgPace,

            @Schema(description = "장착 중인 칭호", example = "런린이")
            String equippedTitleName,

            @Schema(description = "나와의 관계", example = "NONE",
                    allowableValues = {"NONE", "PENDING_SENT", "PENDING_RECEIVED", "FRIEND"})
            String relationStatus
    ) {}

    @Schema(description = "친구 신청 결과")
    @Builder
    public record FollowInfo(
            @Schema(description = "Follow ID (수락·거절·삭제 시 사용)", example = "550e8400-e29b-41d4-a716-446655440000")
            String followId,

            @Schema(description = "신청한 사람 ID", example = "1")
            Long followerId,

            @Schema(description = "신청한 사람 닉네임", example = "내닉네임")
            String followerNickname,

            @Schema(description = "신청받은 사람 ID", example = "2")
            Long followingId,

            @Schema(description = "신청받은 사람 닉네임", example = "달리기왕")
            String followingNickname,

            @Schema(description = "관계 상태", example = "PENDING",
                    allowableValues = {"PENDING", "ACCEPTED"})
            String status,

            @Schema(description = "신청 시각", example = "2026-06-06T13:00:00")
            LocalDateTime createdAt
    ) {}

    @Schema(description = "팔로워·팔로잉 목록 항목")
    public record FollowMemberSummary(
            @Schema(description = "Follow ID (삭제 시 사용)", example = "550e8400-e29b-41d4-a716-446655440000")
            String followId,

            @Schema(description = "회원 ID", example = "2")
            Long memberId,

            @Schema(description = "닉네임", example = "달리기왕")
            String nickname
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
