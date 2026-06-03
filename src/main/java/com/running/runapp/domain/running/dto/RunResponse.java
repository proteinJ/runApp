package com.running.runapp.domain.running.dto;

import com.running.runapp.domain.running.domain.RunningRecord;
import com.running.runapp.domain.running.util.GeometryUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "RunResponse", description = "러닝 API 응답 DTO")
public class RunResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "RunStartResponse", description = "러닝 시작 응답")
    public static class RunStartResponse {

        @Schema(description = "러닝 ID", example = "10")
        private Long runId;

        @Schema(description = "회원 ID", example = "1")
        private Long memberId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "RunFinishResponse", description = "러닝 종료 응답")
    public static class RunFinishResponse {

        @Schema(description = "러닝 ID", example = "10")
        private Long runId;

        @Schema(description = "총 거리(km, 소수 2자리)", example = "3.25")
        private Double totalDistanceKm;

        @Schema(description = "획득 포인트", example = "100")
        private Integer earnedPoints;

        @Schema(description = "평균 페이스", example = "4.00")
        private Double avgPace;
    }

    @Getter
    @Builder
    @Schema(name = "RunDetailResponse", description = "러닝 상세(경로) 응답")
    public static class RunDetailResponse {

        @Schema(description = "러닝 ID", example = "10")
        private Long runId;

        @Schema(
                description = "러닝 경로 좌표 리스트",
                example = "[{\"lat\":35.1126,\"lng\":128.9655},{\"lat\":35.1128,\"lng\":128.9657}]"
        )
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
    @Schema(name = "MyRunSummaryResponse", description = "내 러닝 목록 요약 응답")
    public static class MyRunSummaryResponse {

        @Schema(description = "러닝 ID", example = "10")
        private Long runId;

        @Schema(description = "러닝 시작 시간", example = "2026-05-14T10:00:00")
        private LocalDateTime startTime;

        @Schema(description = "총 거리(km, 소수 2자리)", example = "3.25")
        private Double totalDistanceKm;

        @Schema(description = "평균 페이스", example = "4.00")
        private Double avgPace;

        public static MyRunSummaryResponse from(RunningRecord run) {
            return MyRunSummaryResponse.builder()
                    .runId(run.getId())
                    .startTime(run.getStartTime())
                    .totalDistanceKm(UnitUtils.metersToKm(run.getTotalDistance()))
                    .avgPace(run.getAvgPace())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "MonthlySummaryResponse", description = "월별 러닝 요약 응답")
    public static class MonthlySummaryResponse {

        @Schema(description = "연도", example = "2026")
        private Integer year;

        @Schema(description = "월", example = "5")
        private Integer month;

        @Schema(description = "총 러닝 횟수", example = "12")
        private Integer totalRuns;

        @Schema(description = "총 거리(km)", example = "42.15")
        private Double totalDistanceKm;

        @Schema(description = "평균 거리(km)", example = "3.51")
        private Double avgDistanceKm;

        @Schema(description = "최장 거리(km)", example = "10.00")
        private Double bestDistanceKm;

        @Schema(description = "평균 페이스(초/km)", example = "360")
        private Integer avgPaceSecPerKm;

        @Schema(description = "평균 페이스 표시(mm:ss)", example = "06:00")
        private String avgPaceText;

        @Schema(description = "획득 포인트 합", example = "250")
        private Integer earnedPoints;
    }
}