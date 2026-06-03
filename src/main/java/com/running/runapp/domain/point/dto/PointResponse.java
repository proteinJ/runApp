package com.running.runapp.domain.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class PointResponse {

    @Builder
    @Schema(name = "CurrentPointResult", description = "현재 포인트 조회 응답")
    public record CurrentPointResult(
            @Schema(description = "현재 보유 포인트", example = "250")
            Integer currentTotalPoints
    ) {}
}
