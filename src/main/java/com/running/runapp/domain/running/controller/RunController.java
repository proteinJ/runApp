package com.running.runapp.domain.running.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.running.dto.RunRequest;
import com.running.runapp.domain.running.dto.RunResponse;
import com.running.runapp.domain.running.service.RunService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RunController {

    private final RunService runService;

    /**
     * 러닝 시작
     * POST /api/v1/runs/start
     */
    @PostMapping("/runs/start")
    public ResponseEntity<ApiResponse<RunResponse.RunStartResponse>> start(
            @LoginMember Member me,
            @Valid @RequestBody RunRequest.RunStartRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 시작", runService.start(me, request)));
    }

    /**
     * 러닝 종료
     * POST /api/v1/runs/{runId}/finish
     */
    @PostMapping("/runs/{runId}/finish")
    public ResponseEntity<ApiResponse<RunResponse.RunFinishResponse>> finish(
            @LoginMember Member me,
            @PathVariable Long runId,
            @Valid @RequestBody RunRequest.RunFinishRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 종료", runService.finish(me, runId, request)));
    }

    /**
     * 내 러닝 목록 조회
     * GET /api/v1/members/me/runs
     */
    @GetMapping("/members/me/runs")
    public ResponseEntity<ApiResponse<List<RunResponse.MyRunSummaryResponse>>> myRuns(
            @LoginMember Member me
    ) {
        return ResponseEntity.ok(ApiResponse.success("내 러닝 목록", runService.myRuns(me)));
    }

    /**
     * 러닝 상세 조회
     * GET /api/v1/runs/{runId}
     */
    @GetMapping("/runs/{runId}")
    public ResponseEntity<ApiResponse<RunResponse.RunDetailResponse>> detail(
            @LoginMember Member me,
            @PathVariable Long runId
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 상세", runService.detail(me, runId)));
    }

    /**
     * 월별 러닝 요약
     * GET /api/v1/members/me/runs/monthly-summary?year=2026&month=3
     */
    @GetMapping("/members/me/runs/monthly-summary")
    public ResponseEntity<ApiResponse<RunResponse.MonthlySummaryResponse>> monthlySummary(
            @LoginMember Member me,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("월별 러닝 요약 조회 완료", runService.monthlySummary(me, year, month))
        );
    }
}