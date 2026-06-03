package com.running.runapp.domain.point.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.point.dto.PointResponse;
import com.running.runapp.domain.point.service.PointService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Point", description = "포인트")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointController {

    private final PointService pointService;

    @Operation(summary = "현재 포인트 조회", description = "현재 로그인한 사용자의 보유 포인트를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PointResponse.CurrentPointResult>> getCurrentPoints(
            @LoginMember Member me
    ) {
        PointResponse.CurrentPointResult result = pointService.getCurrentPoints(me);
        return ResponseEntity.ok(ApiResponse.success("현재 포인트 조회 성공", result));
    }
}
