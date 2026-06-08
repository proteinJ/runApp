package com.running.runapp.domain.spot.controller;

import com.running.runapp.domain.spot.dto.SpotRequest;
import com.running.runapp.domain.spot.dto.SpotResponse;
import com.running.runapp.domain.spot.service.SpotService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Spot", description = "스팟 조회, 체크인, 점령/탈환")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/spots")
public class SpotController {

    private final SpotService spotService;

    @Operation(
            summary = "Spot 상세 조회",
            description = "스팟 기본 정보와 현재 점령자 정보를 조회합니다. 점령자는 해당 스팟 누적 체크인 수가 가장 많은 회원입니다."
    )
    @GetMapping("/{spotId}")
    public ResponseEntity<ApiResponse<SpotResponse.DetailInfo>> getSpot(
            @Parameter(description = "스팟 ID", example = "32")
            @PathVariable Long spotId
    ) {
        SpotResponse.DetailInfo spotInfoResponse = spotService.spotInfoResponse(spotId);
        return ResponseEntity.ok(ApiResponse.success("Spot 상세 정보 불러오기 완료", spotInfoResponse));
    }

    @Operation(
            summary = "Spot 체크인",
            description = """
                    러닝 중인 기록으로 스팟에 체크인합니다.
                    체크인 성공 시 스팟 기본 포인트와 경험치를 지급하고, 체크인 수 기준으로 점령자를 재계산합니다.
                    기존 점령자가 없는 상태에서 단독 1등이 되면 SPOT_OCCUPY 보상 50포인트를 지급합니다.
                    다른 회원의 점령 스팟을 단독 1등으로 탈환하면 SPOT_STEAL 보상 100포인트를 지급합니다.
                    체크인 수가 동점이면 기존 점령자를 유지하고 점령 보상은 지급하지 않습니다.
                    """
    )
    @PostMapping("/{spotId}/checkin")
    public ResponseEntity<ApiResponse<SpotResponse.SpotCheckinResponse>> checkin(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "체크인할 스팟 ID", example = "32")
            @PathVariable Long spotId,
            @RequestBody @Valid SpotRequest.SpotCheckinRequest dto
    ) {
        SpotResponse.SpotCheckinResponse spotCheckinResponse =
                spotService.spotCheckin(spotId, dto, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("체크인 완료", spotCheckinResponse));
    }

    @Operation(summary = "Spot 체크인 쿨타임 조회", description = "현재 로그인한 회원의 24시간 체크인 쿨타임 목록을 조회합니다.")
    @GetMapping("/cooldowns")
    public ResponseEntity<ApiResponse<List<SpotResponse.CooldownInfo>>> getCooldowns(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        List<SpotResponse.CooldownInfo> cooldowns = spotService.getCooldowns(principal.getMemberId());
        return ResponseEntity.ok(ApiResponse.success("체크인 쿨타임 조회 완료", cooldowns));
    }

    @Operation(summary = "주변 Spot 조회", description = "현재 위치 기준 주변 스팟과 체크인 가능 여부를 조회합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<SpotResponse.SummaryInfo>>> getNearbySpots(
            @ModelAttribute SpotRequest.NearbySpotsRequest dto,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        List<SpotResponse.SummaryInfo> nearbySpots =
                spotService.getNearbySpots(dto, principal.getMemberId());

        return ResponseEntity.ok(ApiResponse.success("내 주변 스팟 조회 완료", nearbySpots));
    }
}
