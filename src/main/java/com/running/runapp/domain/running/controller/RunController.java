package com.running.runapp.domain.running.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.running.dto.RunResponse;
import com.running.runapp.domain.running.dto.RunRequest;
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

    @PostMapping("/runs/start")
    public ResponseEntity<ApiResponse<RunResponse.RunStartResponse>> start(
            @Valid @RequestBody RunRequest.RunStartRequest request,
            @LoginMember Member member
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 시작", runService.start(request, member)));
    }

    @PostMapping("/runs/{runId}/finish")
    public ResponseEntity<ApiResponse<RunResponse.RunFinishResponse>> finish(
            @PathVariable Long runId,
            @Valid @RequestBody RunRequest.RunFinishRequest request,
            @LoginMember Member member
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 종료", runService.finish(runId, request, member)));
    }

    @GetMapping("/members/me/runs")
    public ResponseEntity<ApiResponse<List<RunResponse.MyRunSummaryResponse>>> myRuns(@LoginMember Member member) {
        return ResponseEntity.ok(ApiResponse.success("내 러닝 목록 조회 완료", runService.myRuns(member)));
    }

    @GetMapping("/runs/{runId}")
    public ResponseEntity<ApiResponse<RunResponse.RunDetailResponse>> detail(
            @PathVariable Long runId,
            @LoginMember Member member
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 상세 조회 완료", runService.detail(runId, member)));
    }
}