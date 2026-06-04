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
            Double latitude,
            Double longitude,
            Double radius
    ) {
    }

    public record SpotCheckinRequest(
            @NotNull
            Long runId,

            @NotNull
            @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
            Double latitude,

            @NotNull
            @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
            Double longitude,

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
