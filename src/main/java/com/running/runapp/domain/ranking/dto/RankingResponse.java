package com.running.runapp.domain.ranking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "RankingResponse", description = "랭킹 조회 응답 DTO")
public class RankingResponse {

    @Builder
    public record Item(
            @Schema(description = "회원 ID", example = "1")
            Long memberId,

            @Schema(description = "닉네임", example = "철수짱")
            String nickname,

            @Schema(description = "기간 내 획득 포인트 합", example = "1200")
            Integer totalPoints,

            @Schema(description = "순위(1부터 시작)", example = "1")
            Integer rank
    ) {}

    @Builder
    public record Result(
            @Schema(description = "랭킹 타입", example = "WEEKLY", allowableValues = {"WEEKLY", "MONTHLY"})
            String type,

            @Schema(description = "기간 시작(포함)", example = "2026-05-04T00:00:00")
            LocalDateTime periodStart,

            @Schema(description = "기간 끝(미포함)", example = "2026-05-11T00:00:00")
            LocalDateTime periodEnd,

            @Schema(description = "랭킹 항목 리스트")
            List<Item> items
    ) {}
}