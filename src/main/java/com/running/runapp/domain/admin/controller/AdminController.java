package com.running.runapp.domain.admin.controller;

import com.running.runapp.domain.admin.dto.AdminMemberResponse;
import com.running.runapp.domain.admin.dto.AdminSpotRequest;
import com.running.runapp.domain.admin.service.AdminService;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.domain.Role;
import com.running.runapp.domain.profile.dto.TitleRequest;
import com.running.runapp.domain.profile.dto.TitleResponse;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    private void requireAdmin(Member me) {
        if (me.getRole() != Role.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }

    // ===================== Member =====================

    @GetMapping("/members")
    public ResponseEntity<?> getMembers(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        Page<AdminMemberResponse.Summary> result = adminService.getMembers(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("회원 목록 조회 완료", result));
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<?> getMemberDetail(@PathVariable Long memberId) {
        AdminMemberResponse.Detail result = adminService.getMemberDetail(memberId);
        return ResponseEntity.ok(ApiResponse.success("회원 상세 조회 완료", result));
    }

    // ===================== Spot   ==== =====================

    @PostMapping("/spots")
    public ResponseEntity<?> createSpot(
            @LoginMember Member me,
            @RequestBody @Valid AdminSpotRequest.Create dto
    ) {
        requireAdmin(me);
        Long spotId = adminService.createSpot(dto);
        return ResponseEntity.ok(ApiResponse.success("스팟 생성 완료", spotId));
    }

    @PatchMapping("/spots/{spotId}")
    public ResponseEntity<?> updateSpot(
            @LoginMember Member me,
            @PathVariable Long spotId,
            @RequestBody @Valid AdminSpotRequest.Update dto
    ) {
        requireAdmin(me);
        Long updatedId = adminService.updateSpot(spotId, dto);
        return ResponseEntity.ok(ApiResponse.success("스팟 수정 완료", updatedId));
    }

    @DeleteMapping("/spots/{spotId}")
    public ResponseEntity<?> deleteSpot(
            @LoginMember Member me,
            @PathVariable Long spotId
    ) {
        requireAdmin(me);
        Long deletedId = adminService.deleteSpot(spotId);
        return ResponseEntity.ok(ApiResponse.success("스팟 삭제 완료", deletedId));
    }

    // ===================== Title  =====================

    @Operation(summary = "칭호 추가", description = "관리자용 칭호 추가")
    @PostMapping("/title/add")
    public ResponseEntity<ApiResponse<TitleResponse.TitleInfo>> addNewTitle(
            @LoginMember Member me,
            @RequestBody @Valid TitleRequest.addNewTitle dto
    ) {
        requireAdmin(me);
        TitleResponse.TitleInfo title = adminService.addNewTitle(dto);
        return ResponseEntity.ok(ApiResponse.success("새로운 칭호 추가 성공", title));
    }

    @Operation(summary = "칭호 정보 수정", description = "관리자용 칭호 정보 수정")
    @PatchMapping("/title/update")
    public ResponseEntity<ApiResponse<TitleResponse.TitleInfo>> updateTitleInfo(
            @LoginMember Member me,
            @RequestBody TitleRequest.updateTitleInfo dto
    ) {
        requireAdmin(me);
        TitleResponse.TitleInfo title = adminService.updateTitleInfo(dto);
        return ResponseEntity.ok(ApiResponse.success("칭호 정보 수정 성공", title));
    }

    @Operation(summary = "칭호 삭제", description = "관리자용 칭호 삭제")
    @DeleteMapping("/title/delete")
    public ResponseEntity<ApiResponse<String>> deleteTitle(
            @LoginMember Member me,
            @RequestParam("titleId") Long titleId
    ) {
        requireAdmin(me);
        adminService.deleteTitle(titleId);
        return ResponseEntity.ok(ApiResponse.success("칭호 삭제 성공"));
    }
}