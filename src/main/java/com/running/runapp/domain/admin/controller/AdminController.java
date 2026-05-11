package com.running.runapp.domain.admin.controller;

import com.running.runapp.domain.admin.dto.AdminMemberResponse;
import com.running.runapp.domain.admin.dto.AdminSpotRequest;
import com.running.runapp.domain.admin.dto.AdminSpotResponse;
import com.running.runapp.domain.admin.service.AdminService;
import com.running.runapp.global.common.ApiResponse;
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

    // ===================== Member =====================

    // 회원 목록 조회 (페이징 + 검색)
    @GetMapping("/members")
    public ResponseEntity<?> getMembers(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        Page<AdminMemberResponse.Summary> result = adminService.getMembers(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("회원 목록 조회 완료", result));
    }

    // 회원 상세 조회
    @GetMapping("/members/{memberId}")
    public ResponseEntity<?> getMemberDetail(@PathVariable Long memberId) {
        AdminMemberResponse.Detail result = adminService.getMemberDetail(memberId);
        return ResponseEntity.ok(ApiResponse.success("회원 상세 조회 완료", result));
    }

    // ===================== Spot =====================

    // 스팟 목록 조회 (페이징 + 검색)
    @GetMapping("/spots")
    public ResponseEntity<?> getSpots(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        Page<AdminSpotResponse.Summary> result = adminService.getSpots(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("스팟 목록 조회 완료", result));
    }

    // 스팟 상세 조회
    @GetMapping("/spots/{spotId}")
    public ResponseEntity<?> getSpotDetail(@PathVariable Long spotId) {
        AdminSpotResponse.Detail result = adminService.getSpotDetail(spotId);
        return ResponseEntity.ok(ApiResponse.success("스팟 상세 조회 완료", result));
    }

    // 스팟 생성
    @PostMapping("/spots")
    public ResponseEntity<?> createSpot(@RequestBody @Valid AdminSpotRequest.Create dto) {
        Long spotId = adminService.createSpot(dto);
        return ResponseEntity.ok(ApiResponse.success("스팟 생성 완료", spotId));
    }

    // 스팟 수정
    @PatchMapping("/spots/{spotId}")
    public ResponseEntity<?> updateSpot(
            @PathVariable Long spotId,
            @RequestBody @Valid AdminSpotRequest.Update dto
    ) {
        Long updatedId = adminService.updateSpot(spotId, dto);
        return ResponseEntity.ok(ApiResponse.success("스팟 수정 완료", updatedId));
    }

    // 스팟 삭제
    @DeleteMapping("/spots/{spotId}")
    public ResponseEntity<?> deleteSpot(@PathVariable Long spotId) {
        Long deletedId = adminService.deleteSpot(spotId);
        return ResponseEntity.ok(ApiResponse.success("스팟 삭제 완료", deletedId));
    }
}