package com.running.runapp.domain.ghostRun.dto;

import com.running.runapp.domain.running.dto.LatLng;
import com.running.runapp.domain.running.dto.RunRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "GhostRequest", description = "고스트런 API 요청 DTO")
public class GhostRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "GhostRunStartRequest", description = "고스트런 시작 요청")
    public static class GhostRunStartRequest {

        @NotNull
        @Schema(description = "도전할 고스트 랭킹 ID", example = "1")
        private Long ghostRankingId;

        @NotNull
        @Schema(description = "현재 위치 위도", example = "37.51231")
        private Double currentLat;

        @NotNull
        @Schema(description = "현재 위치 경도", example = "127.10232")
        private Double currentLng;

        @NotNull
        @Schema(description = "러닝 시작 시간", example = "2026-06-21T18:00:00")
        private LocalDateTime startTime;

        public RunRequest.RunStartRequest toRunStartRequest() {
            return new RunRequest.RunStartRequest(startTime);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "GhostRunFinishRequest", description = "고스트런 종료 요청")
    public static class GhostRunFinishRequest {

        @NotNull
        @Schema(description = "도전한 고스트 랭킹 ID", example = "1")
        private Long ghostRankingId;

        @NotNull
        @Schema(description = "러닝 종료 시간", example = "2026-06-21T18:27:30")
        private LocalDateTime endTime;

        @NotNull
        @Schema(description = "GPS 기반 실제 러닝 시작 시간", example = "2026-06-21T18:00:10")
        private LocalDateTime realStartTime;

        @Schema(
                description = "러닝 경로 좌표 리스트",
                example = "[{\"lat\":37.5123,\"lng\":127.1023},{\"lat\":37.5128,\"lng\":127.1031}]"
        )
        private List<LatLng> path;

        public RunRequest.RunFinishRequest toRunFinishRequest() {
            return new RunRequest.RunFinishRequest(endTime, realStartTime, path);
        }
    }
}
