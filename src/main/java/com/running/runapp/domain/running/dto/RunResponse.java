// RunResponse.java
package com.running.runapp.domain.running.dto;

import com.running.runapp.domain.running.domain.RunningRecord;
import com.running.runapp.domain.running.util.GeometryUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class RunResponse {

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
        private Double totalDistanceKm;
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
        private Double totalDistanceKm;

        public static MyRunSummaryResponse from(RunningRecord run) {
            return MyRunSummaryResponse.builder()
                    .runId(run.getId())
                    .startTime(run.getStartTime())
                    .totalDistanceKm(UnitUtils.metersToKm(run.getTotalDistance()))
                    .build();
        }
    }

    // 월별 요약
    @Getter
    @Builder
    @AllArgsConstructor
    public static class MonthlySummaryResponse {
        private Integer year;
        private Integer month;
        private Integer totalRuns;

        private Double totalDistanceKm;
        private Double avgDistanceKm;
        private Double bestDistanceKm;

        // ✅ 실무형: 없으면 null
        private Integer avgPaceSecPerKm;
        // ✅ 실무형: "mm:ss" 형태, 없으면 null
        private String avgPaceText;

        private Integer earnedPoints;
    }
}