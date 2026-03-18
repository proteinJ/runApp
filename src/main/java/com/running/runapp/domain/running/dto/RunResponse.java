package com.running.runapp.domain.running.dto;

import com.running.runapp.domain.running.domain.RunningRecord;
import com.running.runapp.domain.running.util.GeometryUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class RunResponse
{
    @Getter
    @Builder
    @AllArgsConstructor
    public static class RunStartResponse {
        private Long runId;
        private Long memberId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RunFinishResponse {
        private Long runId;
        private Double totalDistance;
        private Integer earnedPoints;
    }

    @Getter
    @Builder
    public static class RunDetailResponse {
        private Long runId;
        private List<LatLng> path;

        public static RunDetailResponse from(RunningRecord record) {
            return RunDetailResponse.builder()
                    .runId(record.getId())
                    .path(GeometryUtils.toLatLngList(record.getPath()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MyRunSummaryResponse {
        private Long runId;
        private LocalDateTime startTime;
        private Double totalDistance;

        public static MyRunSummaryResponse from(RunningRecord run) {
            return MyRunSummaryResponse.builder()
                    .runId(run.getId())
                    .startTime(run.getStartTime())
                    .totalDistance(run.getTotalDistance())
                    .build();
        }
    }
}
