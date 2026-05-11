package com.running.runapp.domain.ranking.controller;

import com.running.runapp.domain.ranking.dto.RankingResponse;
import com.running.runapp.domain.ranking.service.RankingService;
import com.running.runapp.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rankings")
public class RankingController {

    private final RankingService rankingService;

    /**
     * 주간 랭킹 (월요일 시작, 전체 사용자, 포인트 기준)
     * GET /api/v1/rankings/weekly?limit=50
     */
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<RankingResponse.Result>> weekly(
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("주간 랭킹 조회 성공", rankingService.weeklyRanking(limit))
        );
    }

    /**
     * 월간 랭킹 (전체 사용자, 포인트 기준)
     * GET /api/v1/rankings/monthly?year=2026&month=5&limit=50
     * year/month 없으면 이번달 기준
     */
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<RankingResponse.Result>> monthly(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("월간 랭킹 조회 성공", rankingService.monthlyRanking(year, month, limit))
        );
    }
}