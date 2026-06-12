package com.running.runapp.domain.spot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

public class SpotRequest {
    public record NearbySpotsRequest(
            @Schema(description = "현재 위도", example = "35.246509")
            Double latitude,
            @Schema(description = "현재 경도", example = "129.091786")
            Double longitude,
            @Schema(description = "검색 반경(m). 없으면 1000m", example = "1000")
            Double radius
    ) {
    }

    @Schema(name = "SpotCheckinRequest", description = "스팟 체크인 요청")
    public record SpotCheckinRequest(
            @Schema(description = "현재 RUNNING 상태인 러닝 기록 ID", example = "46")
            @NotNull
            Long runId,

            @Schema(description = "체크인 요청 위치 위도", example = "35.246509")
            @NotNull
            @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
            Double latitude,

            @Schema(description = "체크인 요청 위치 경도", example = "129.091786")
            @NotNull
            @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
            Double longitude,

            @Schema(description = "클라이언트 기준 체크인 시각", example = "2026-06-08T13:30:00")
            LocalDateTime timestamp
    ) {
    }

    @Builder
    public record SpotCreateRequest(
            String name,
            String description,

            @Min(0) @Max(100)
            Integer rewardAmount,

            Point location,
            double latitude,
            double longitude,

            @Schema(description = "보상 경험치량")
            @Min(0) @Max(200)
            Long expAmount
    ) {
    }

    public record SpotUpdateRequest(
            String name,
            String description,

            @Min(0) @Max(10000)
            Integer rewardAmount,

            Point location,
            Double latitude,
            Double longitude
    ) {
    }

}
