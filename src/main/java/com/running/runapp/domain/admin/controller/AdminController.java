package com.running.runapp.domain.admin.controller;

import com.running.runapp.domain.admin.dto.*;
import com.running.runapp.domain.admin.service.AdminService;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.domain.Role;
import com.running.runapp.domain.notice.dto.NoticeResponse;
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

import java.util.List;

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

    // 회원 목록 조회
    @GetMapping("/members")
    public ResponseEntity<?> getMembers(
            @LoginMember Member me,
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        requireAdmin(me);
        Page<AdminMemberResponse.Summary> result = adminService.getMembers(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("회원 목록 조회 완료", result));
    }

    // 회원 상세 조회
    @GetMapping("/members/{memberId}")
    public ResponseEntity<?> getMemberDetail(
            @LoginMember Member me,
            @PathVariable Long memberId
    ) {
        requireAdmin(me);
        AdminMemberResponse.Detail result = adminService.getMemberDetail(memberId);
        return ResponseEntity.ok(ApiResponse.success("회원 상세 조회 완료", result));
    }

    // ===================== Spot =====================

    // 스팟 목록 조회
    @GetMapping("/spots")
    public ResponseEntity<ApiResponse<Page<AdminSpotResponse.Summary>>> getSpots(
            @LoginMember Member me,
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        requireAdmin(me);
        Page<AdminSpotResponse.Summary> result = adminService.getSpots(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("스팟 목록 조회 완료", result));
    }

    // 스팟 상세 조회
    @GetMapping("/spots/{spotId}")
    public ResponseEntity<ApiResponse<AdminSpotResponse.Detail>> getSpotDetail(
            @LoginMember Member me,
            @PathVariable Long spotId
    ) {
        requireAdmin(me);
        AdminSpotResponse.Detail result = adminService.getSpotDetail(spotId);
        return ResponseEntity.ok(ApiResponse.success("스팟 상세 조회 완료", result));
    }

    // 스팟 생성
    @PostMapping("/spots")
    public ResponseEntity<?> createSpot(
            @LoginMember Member me,
            @RequestBody @Valid AdminSpotRequest.Create dto
    ) {
        requireAdmin(me);
        Long spotId = adminService.createSpot(dto);
        return ResponseEntity.ok(ApiResponse.success("스팟 생성 완료", spotId));
    }

    // 스팟 수정
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

    // 스팟 삭제
    @DeleteMapping("/spots/{spotId}")
    public ResponseEntity<?> deleteSpot(
            @LoginMember Member me,
            @PathVariable Long spotId
    ) {
        requireAdmin(me);
        Long deletedId = adminService.deleteSpot(spotId);
        return ResponseEntity.ok(ApiResponse.success("스팟 삭제 완료", deletedId));
    }

    // ===================== Shop =====================

    // 상품 목록 조회
    @GetMapping("/shop/items")
    public ResponseEntity<ApiResponse<List<AdminShopResponse.Item>>> getShopItems(
            @LoginMember Member me
    ) {
        requireAdmin(me);
        List<AdminShopResponse.Item> result = adminService.getShopItems();
        return ResponseEntity.ok(ApiResponse.success("상품 목록 조회 완료", result));
    }

    // 상품 수정
    @PatchMapping("/shop/items/{itemId}")
    public ResponseEntity<ApiResponse<AdminShopResponse.Item>> updateShopItem(
            @LoginMember Member me,
            @PathVariable Long itemId,
            @RequestBody @Valid AdminShopRequest.UpdateItem dto
    ) {
        requireAdmin(me);
        AdminShopResponse.Item result = adminService.updateShopItem(itemId, dto);
        return ResponseEntity.ok(ApiResponse.success("상품 수정 완료", result));
    }

    // 상품 판매상태 수정
    @PatchMapping("/shop/items/{itemId}/active")
    public ResponseEntity<ApiResponse<AdminShopResponse.Item>> updateShopItemActive(
            @LoginMember Member me,
            @PathVariable Long itemId,
            @RequestBody @Valid AdminShopRequest.UpdateItemActive dto
    ) {
        requireAdmin(me);
        AdminShopResponse.Item result = adminService.updateShopItemActive(itemId, dto);
        return ResponseEntity.ok(ApiResponse.success("상품 판매상태 수정 완료", result));
    }

    // ===================== GroupRunning =====================

    // 그룹 상태 변경
    @PatchMapping("/groups/{groupId}/status")
    public ResponseEntity<ApiResponse<AdminGroupResponse.StatusResult>> updateGroupStatus(
            @LoginMember Member me,
            @PathVariable Long groupId,
            @RequestBody @Valid AdminGroupRequest.UpdateStatus dto
    ) {
        requireAdmin(me);
        AdminGroupResponse.StatusResult result = adminService.updateGroupStatus(groupId, dto);
        return ResponseEntity.ok(ApiResponse.success("그룹 상태 변경 완료", result));
    }

    // ===================== Title =====================

    // 칭호 추가
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

    // 칭호 수정
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

    // 칭호 삭제
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

    // ===================== Notice =====================

    @Operation(summary = "공지사항 생성")
    @PostMapping("/notices")
    public ResponseEntity<ApiResponse<NoticeResponse.Detail>> createNotice(
            @LoginMember Member me,
            @RequestBody @Valid AdminNoticeRequest.create dto
    ) {
        requireAdmin(me);
        return ResponseEntity.ok(ApiResponse.success("공지사항 생성 완료", adminService.createNotice(dto)));
    }

    @Operation(summary = "공지사항 수정")
    @PatchMapping("/notices/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponse.Detail>> updateNotice(
            @LoginMember Member me,
            @PathVariable Long noticeId,
            @RequestBody AdminNoticeRequest.update dto
    ) {
        requireAdmin(me);
        return ResponseEntity.ok(ApiResponse.success("공지사항 수정 완료", adminService.updateNotice(noticeId, dto)));
    }

    @Operation(summary = "공지사항 삭제")
    @DeleteMapping("/notices/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @LoginMember Member me,
            @PathVariable Long noticeId
    ) {
        requireAdmin(me);
        adminService.deleteNotice(noticeId);
        return ResponseEntity.ok(ApiResponse.success("공지사항 삭제 완료"));
    }

    @Operation(summary = "칭호 지급", description = "관리자용 유저에게 칭호 지급")
    @PostMapping("/title/grant")
    public ResponseEntity<ApiResponse<TitleResponse.TitleInfo>> grantTitleToUser(
            @LoginMember Member me,
            @RequestBody @Valid TitleRequest.addTitleToUser dto
    ) {
        requireAdmin(me);
        TitleResponse.TitleInfo titleInfo = adminService.grantTitleToUser(me.getProfile(), dto.titleCode());
        return ResponseEntity.ok(ApiResponse.success("칭호 획득 완료!", titleInfo));
    }
}
