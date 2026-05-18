package com.running.runapp.domain.profile.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.dto.SocialRequest;
import com.running.runapp.domain.profile.dto.SocialResponse;
import com.running.runapp.domain.profile.service.FollowService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follows")
public class FollowController {

    private final FollowService followService;

    // 친구 신청
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<SocialResponse.FollowInfo>> request(
            @LoginMember Member me,
            @RequestBody @Valid SocialRequest.FollowSend dto
    ) {
        SocialResponse.FollowInfo res = followService.requestFollow(me, dto);
        return ResponseEntity.ok(ApiResponse.success("친구 신청 완료", res));
    }

    // 친구 신청 수락
    @PostMapping("/{followId}/accept")
    public ResponseEntity<ApiResponse<Void>> accept(
            @LoginMember Member me,
            @PathVariable String followId
    ) {
        followService.accept(me, followId);
        return ResponseEntity.ok(ApiResponse.success("친구 신청 수락 완료"));
    }

    // 친구 신청 거절
    @PostMapping("/{followId}/reject")
    public ResponseEntity<ApiResponse<Void>> reject(
            @LoginMember Member me,
            @PathVariable String followId
    ) {
        followService.reject(me, followId);
        return ResponseEntity.ok(ApiResponse.success("친구 신청 거절 완료"));
    }
    // 친구 삭제 및 신청 취소 (언팔로우)
    @DeleteMapping("/{followId}")
    public ResponseEntity<ApiResponse<Void>> deleteFollow(
            @LoginMember Member me,
            @PathVariable String followId
    ) {
        followService.deleteFollow(me, followId);
        return ResponseEntity.ok(ApiResponse.success("친구 관계가 해제되었습니다."));
    }
}