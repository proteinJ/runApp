package com.running.runapp.domain.running.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.running.dto.LocationMessage;
import com.running.runapp.domain.running.dto.RunRequest;
import com.running.runapp.domain.running.dto.RunResponse;
import com.running.runapp.domain.running.service.RunService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Run", description = "러닝 기록")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RunController {

    private final RunService runService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "러닝 시작", description = "러닝을 시작합니다.")
    @PostMapping("/runs/start")
    public ResponseEntity<ApiResponse<RunResponse.RunStartResponse>> start(
            @LoginMember Member me,
            @Valid @RequestBody RunRequest.RunStartRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 시작", runService.start(me, request)));
    }

    @Operation(summary = "러닝 종료", description = "러닝을 종료하고 경로 기반 거리/포인트를 저장합니다.")
    @PostMapping("/runs/{runId}/finish")
    public ResponseEntity<ApiResponse<RunResponse.RunFinishResponse>> finish(
            @LoginMember Member me,
            @PathVariable Long runId,
            @Valid @RequestBody RunRequest.RunFinishRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 종료", runService.finish(me, runId, request)));
    }

    @Operation(summary = "내 러닝 목록 조회", description = "내 러닝 목록을 조회합니다.")
    @GetMapping("/members/me/runs")
    public ResponseEntity<ApiResponse<List<RunResponse.MyRunSummaryResponse>>> myRuns(
            @LoginMember Member me
    ) {
        return ResponseEntity.ok(ApiResponse.success("내 러닝 목록", runService.myRuns(me)));
    }

    @Operation(summary = "러닝 상세 조회", description = "러닝 상세(경로)를 조회합니다.")
    @GetMapping("/runs/{runId}")
    public ResponseEntity<ApiResponse<RunResponse.RunDetailResponse>> detail(
            @LoginMember Member me,
            @PathVariable Long runId
    ) {
        return ResponseEntity.ok(ApiResponse.success("러닝 상세", runService.detail(me, runId)));
    }

    @Operation(summary = "월별 러닝 요약", description = "year/month 기준 월별 요약을 조회합니다.")
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

    // 클라이언트가 /app/location/{groupId} 로 메시지를 쏘면 여기가 받음
    @MessageMapping("/location/{groupId}")
    public void sendLocation(@DestinationVariable Long groupId, LocationMessage message) {
        message.setGroupId(groupId);

        // Redis의 특정 방 채널로 퍼블리싱
        ChannelTopic topic = new ChannelTopic("group:" + groupId);
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
