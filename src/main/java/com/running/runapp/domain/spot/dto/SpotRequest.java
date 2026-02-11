package com.running.runapp.domain.spot.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
            Long runId,
            double latitude,
            double longitude,
            LocalDateTime timestamp

    ) {
    }

    @Builder
    public record SpotCreateRequest(
            String name,
            String description,

            @Min(0) @Max(10000)
            Integer rewardAmount,

            Point location,
            double latitude,
            double longitude
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
