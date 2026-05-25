package com.running.runapp.domain.ranking.controller;

import com.running.runapp.domain.ranking.dto.RankingResponse;
import com.running.runapp.domain.ranking.service.RankingService;
import com.running.runapp.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Ranking", description = "주간/월간 랭킹")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rankings")
public class RankingController {

    private final RankingService rankingService;

    @Operation(summary = "주간 랭킹", description = "월요일 시작 기준 주간 포인트 랭킹(전체 사용자)")
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<RankingResponse.Result>> weekly(
            @Parameter(description = "조회 개수(기본값 서버에서 처리)", example = "50")
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("주간 랭킹 조회 성공", rankingService.weeklyRanking(limit))
        );
    }

    @Operation(summary = "월간 랭킹", description = "월간 포인트 랭킹(전체 사용자). year/month 없으면 이번달")
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<RankingResponse.Result>> monthly(
            @Parameter(description = "연도(없으면 현재 연도)", example = "2026")
            @RequestParam(required = false) Integer year,
            @Parameter(description = "월(1~12, 없으면 현재 월)", example = "5")
            @RequestParam(required = false) Integer month,
            @Parameter(description = "조회 개수(기본값 서버에서 처리)", example = "50")
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("월간 랭킹 조회 성공", rankingService.monthlyRanking(year, month, limit))
        );
    }
}