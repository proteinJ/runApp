package com.running.runapp.domain.ranking.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class RankingResponse {

    @Builder
    public record Item(
            Long memberId,
            String nickname,
            Integer totalPoints,
            Integer rank
    ) {}

    @Builder
    public record Result(
            String type,                 // WEEKLY / MONTHLY
            LocalDateTime periodStart,   // inclusive
            LocalDateTime periodEnd,     // exclusive
            List<Item> items
    ) {}
}