package com.running.runapp.domain.profile.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.dto.SocialRequest;
import com.running.runapp.domain.profile.dto.SocialResponse;
import com.running.runapp.domain.profile.service.FollowService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Follow", description = "친구 신청·수락·거절·조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follows")
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "친구 신청", description = "닉네임으로 상대방에게 친구 신청을 보냅니다. 이미 관계가 있으면 거부됩니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            { "targetNickname": "달리기왕" }
                            """)
            )
    )
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<SocialResponse.FollowInfo>> request(
            @LoginMember Member me,
            @RequestBody @Valid SocialRequest.FollowSend dto
    ) {
        SocialResponse.FollowInfo res = followService.requestFollow(me, dto);
        return ResponseEntity.ok(ApiResponse.success("친구 신청 완료", res));
    }

    @Operation(
            summary = "친구 신청 수락",
            description = "받은 친구 신청을 수락합니다. 수락 대상의 followId를 path에 전달하세요."
    )
    @PostMapping("/{followId}/accept")
    public ResponseEntity<ApiResponse<Void>> accept(
            @LoginMember Member me,
            @Parameter(description = "수락할 Follow ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String followId
    ) {
        followService.accept(me, followId);
        return ResponseEntity.ok(ApiResponse.success("친구 신청 수락 완료"));
    }

    @Operation(
            summary = "친구 신청 거절",
            description = "받은 친구 신청을 거절합니다. 거절된 관계는 삭제됩니다."
    )
    @PostMapping("/{followId}/reject")
    public ResponseEntity<ApiResponse<Void>> reject(
            @LoginMember Member me,
            @Parameter(description = "거절할 Follow ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String followId
    ) {
        followService.reject(me, followId);
        return ResponseEntity.ok(ApiResponse.success("친구 신청 거절 완료"));
    }

    @Operation(
            summary = "받은 친구 신청 목록 조회",
            description = "아직 수락하지 않은 PENDING 상태의 친구 신청 목록을 반환합니다. followId로 수락·거절 가능합니다."
    )
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<SocialResponse.FollowMemberSummary>>> getPendingRequests(
            @LoginMember Member me
    ) {
        List<SocialResponse.FollowMemberSummary> res = followService.getPendingRequests(me.getId());
        return ResponseEntity.ok(ApiResponse.success("받은 친구 신청 목록 조회 완료", res));
    }

    @Operation(
            summary = "내 팔로워 목록 조회",
            description = "나를 팔로우하는 친구 목록을 반환합니다. ACCEPTED 상태인 관계만 포함됩니다."
    )
    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<List<SocialResponse.FollowMemberSummary>>> getFollowers(
            @LoginMember Member me
    ) {
        List<SocialResponse.FollowMemberSummary> res = followService.getFollowers(me.getId());
        return ResponseEntity.ok(ApiResponse.success("팔로워 목록 조회 완료", res));
    }

    @Operation(
            summary = "내 팔로잉 목록 조회",
            description = "내가 팔로우하는 친구 목록을 반환합니다. ACCEPTED 상태인 관계만 포함됩니다."
    )
    @GetMapping("/followings")
    public ResponseEntity<ApiResponse<List<SocialResponse.FollowMemberSummary>>> getFollowings(
            @LoginMember Member me
    ) {
        List<SocialResponse.FollowMemberSummary> res = followService.getFollowings(me.getId());
        return ResponseEntity.ok(ApiResponse.success("팔로잉 목록 조회 완료", res));
    }

    @Operation(
            summary = "친구 삭제 / 신청 취소",
            description = "친구 관계를 끊거나 내가 보낸 친구 신청을 취소합니다. 팔로워·팔로잉 양쪽 모두 사용 가능합니다."
    )
    @DeleteMapping("/{followId}")
    public ResponseEntity<ApiResponse<Void>> deleteFollow(
            @LoginMember Member me,
            @Parameter(description = "삭제할 Follow ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String followId
    ) {
        followService.deleteFollow(me, followId);
        return ResponseEntity.ok(ApiResponse.success("친구 관계가 해제되었습니다."));
    }
}
