package com.running.runapp.domain.running.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "RunRequest", description = "러닝 API 요청 DTO")
public class RunRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "RunStartRequest", description = "러닝 시작 요청")
    public static class RunStartRequest {

        @NotNull
        @Schema(description = "러닝 시작 시간", example = "2026-05-14T10:00:00")
        private LocalDateTime startTime;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "RunFinishRequest", description = "러닝 종료 요청")
    public static class RunFinishRequest {

        @NotNull
        @Schema(description = "러닝 종료 시간", example = "2026-05-14T10:30:00")
        private LocalDateTime endTime;

        @NotNull
        @Schema(description = "GPS 기반 실제 러닝 시작 시간 (페이스 계산용)", example = "2026-05-14T10:00:05")
        private LocalDateTime realStartTime;

        @Schema(
                description = "러닝 경로 좌표 리스트 (최소 2개)",
                example = "[{\"lat\":35.1126,\"lng\":128.9655},{\"lat\":35.1128,\"lng\":128.9657}]"
        )
        private List<LatLng> path;
    }
}