package com.running.runapp.domain.running.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LatLng", description = "위경도 좌표")
public record LatLng(
        @Schema(description = "위도(latitude)", example = "35.1126")
        Double lat,

        @Schema(description = "경도(longitude)", example = "128.9655")
        Double lng
) {
}