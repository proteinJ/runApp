package com.running.runapp.domain.spot.controller;

import com.running.runapp.domain.spot.dto.*;
import com.running.runapp.domain.spot.service.SpotService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.security.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/spots")
public class SpotController {

    private final SpotService spotService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createSpot(@RequestBody @Valid SpotRequest.SpotCreateRequest dto) {
        Long SpotId = spotService.createSpot(dto);
        
        return ResponseEntity.ok(ApiResponse.success("Spot 생성 완료 - SpotId: ", SpotId));
    }

    @PatchMapping("{spotId}")
    public ResponseEntity<ApiResponse<Long>> updateSpot(
            @RequestBody @Valid SpotRequest.SpotUpdateRequest dto,
            @PathVariable Long spotId) {
        Long SpotId = spotService.updateSpot(spotId, dto);

        return ResponseEntity.ok(ApiResponse.success("Spot 수정 완료 - SpotId: ", SpotId));
    }


    @DeleteMapping("/{spotId}")
    public ResponseEntity<ApiResponse<Long>> deleteSpot(@PathVariable Long spotId) {
        Long SpotId = spotService.deleteSpot(spotId);

        return ResponseEntity.ok(ApiResponse.success("Spot 삭제 완료 - SpotId: ", SpotId));
    }

    @GetMapping("/{spotId}")
    public ResponseEntity<ApiResponse<SpotResponse.DetailInfo>> getSpot(@PathVariable Long spotId) {
        SpotResponse.DetailInfo spotInfoResponse = spotService.spotInfoResponse(spotId);

        return ResponseEntity.ok(ApiResponse.success("Spot 상세 정보 불러오기 완료", spotInfoResponse));
    }

    @PostMapping("/{spotId}/checkin")
    public ResponseEntity<ApiResponse<SpotResponse.SpotCheckinResponse>> checkin(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long spotId,
            @RequestBody @Valid SpotRequest.SpotCheckinRequest dto
            ) {

        SpotResponse.SpotCheckinResponse spotCheckinResponse = spotService.spotCheckin(spotId, dto, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("체크인 완료", spotCheckinResponse));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<SpotResponse.SummaryInfo>>> getNearbySpots(
            @RequestBody SpotRequest.NearbySpotsRequest dto,
            @AuthenticationPrincipal PrincipalDetails principal) {
        List<SpotResponse.SummaryInfo> nearbySpots = spotService.getNearbySpots(dto, principal.getMemberId());

        return ResponseEntity.ok(ApiResponse.success("내 주변 스팟 조회 완료", nearbySpots));
    }
}
