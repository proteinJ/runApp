package com.running.runapp.domain.ghostRun.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.ghostRun.domain.GhostCategory;
import com.running.runapp.domain.ghostRun.dto.GhostRequest;
import com.running.runapp.domain.ghostRun.dto.GhostResponse;
import com.running.runapp.domain.ghostRun.service.GhostRunService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Ghost Run", description = "고스트런")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class GhostRunController {

    private final GhostRunService ghostRunService;

    @Operation(summary = "고스트 랭킹 조회", description = "현재 위치의 동네 기준으로 거리 부문별 TOP3 고스트 랭킹을 조회합니다.")
    @GetMapping("/ghost-rankings")
    public ResponseEntity<ApiResponse<GhostResponse.GhostRankingListResponse>> rankings(
            @Parameter(description = "현재 위치 위도", example = "37.51231")
            @RequestParam Double lat,
            @Parameter(description = "현재 위치 경도", example = "127.10232")
            @RequestParam Double lng,
            @Parameter(description = "거리 부문", example = "5K")
            @RequestParam GhostCategory category
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("고스트 랭킹 조회 성공", ghostRunService.rankings(lat, lng, category))
        );
    }

    @Operation(summary = "고스트 기록 상세 조회", description = "고스트 기록 상세와 지도 표시용 경로 좌표를 조회합니다.")
    @GetMapping("/ghost-rankings/{rankingId}")
    public ResponseEntity<ApiResponse<GhostResponse.GhostRankingDetailResponse>> detail(
            @Parameter(description = "고스트 랭킹 ID", example = "1")
            @PathVariable Long rankingId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("고스트 기록 상세 조회 성공", ghostRunService.detail(rankingId))
        );
    }

    @Operation(summary = "고스트런 시작", description = "고스트 시작 지점 10m 이내인지 확인한 뒤 개인 러닝을 시작합니다.")
    @PostMapping("/ghost-runs/start")
    public ResponseEntity<ApiResponse<GhostResponse.GhostRunStartResponse>> start(
            @LoginMember Member me,
            @Valid @RequestBody GhostRequest.GhostRunStartRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("고스트런 시작 성공", ghostRunService.start(me, request))
        );
    }

    @Operation(summary = "고스트런 종료", description = "개인 러닝을 종료하고, 도전한 고스트 기록을 이긴 경우 해당 랭킹 자리만 교체합니다.")
    @PostMapping("/ghost-runs/{runId}/finish")
    public ResponseEntity<ApiResponse<GhostResponse.GhostRunFinishResponse>> finish(
            @LoginMember Member me,
            @Parameter(description = "고스트런 시작 API에서 받은 러닝 ID", example = "77")
            @PathVariable Long runId,
            @Valid @RequestBody GhostRequest.GhostRunFinishRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("고스트런 종료 성공", ghostRunService.finish(me, runId, request))
        );
    }
}
