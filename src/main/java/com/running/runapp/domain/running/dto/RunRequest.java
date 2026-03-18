package com.running.runapp.domain.running.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class RunRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RunStartRequest {

        @NotNull
        private LocalDateTime startTime;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RunFinishRequest {

        @NotNull
        private LocalDateTime endTime;

        @NotNull
        @Size(min = 2, message = "path는 최소 2개 좌표가 필요합니다.")
        private List<LatLng> path;
    }
}
