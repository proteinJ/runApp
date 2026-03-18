package com.running.runapp.domain.running.controller;

import com.running.runapp.domain.running.dto.RunResponse;
import com.running.runapp.domain.running.dto.RunRequest;
import com.running.runapp.domain.running.service.RunService;
import com.running.runapp.global.common.ApiResponse;
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

    @PostMapping("/runs/start")
    public ResponseEntity<ApiResponse<RunResponse.RunStartResponse>> start(
            @Valid @RequestBody RunRequest.RunStartRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 시작", runService.start(request)));
    }

    @PostMapping("/runs/{runId}/finish")
    public ResponseEntity<ApiResponse<RunResponse.RunFinishResponse>> finish(
            @PathVariable Long runId,
            @Valid @RequestBody RunRequest.RunFinishRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 종료", runService.finish(runId, request)));
    }

    @GetMapping("/members/me/runs")
    public ResponseEntity<ApiResponse<List<RunResponse.MyRunSummaryResponse>>> myRuns() {
        return ResponseEntity.ok(ApiResponse.success("내 러닝 목록", runService.myRuns()));
    }

    @GetMapping("/runs/{runId}")
    public ResponseEntity<ApiResponse<RunResponse.RunDetailResponse>> detail(
            @PathVariable Long runId
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 상세", runService.detail(runId)));
    }
}