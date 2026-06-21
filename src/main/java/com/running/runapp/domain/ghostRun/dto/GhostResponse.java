package com.running.runapp.domain.ghostRun.dto;

import com.running.runapp.domain.ghostRun.domain.GhostCategory;
import com.running.runapp.domain.ghostRun.domain.GhostRanking;
import com.running.runapp.domain.running.domain.RunningRecord;
import com.running.runapp.domain.running.dto.LatLng;
import com.running.runapp.domain.running.dto.UnitUtils;
import com.running.runapp.domain.running.util.GeometryUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(name = "GhostResponse", description = "고스트런 API 응답 DTO")
public class GhostResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "GhostRankingListResponse", description = "고스트 랭킹 목록 응답")
    public static class GhostRankingListResponse {
        @Schema(description = "주소 동", example = "잠실동")
        private String addressDong;

        @Schema(description = "거리 부문", example = "5K")
        private GhostCategory category;

        @Schema(description = "TOP3 고스트 랭킹 목록")
        private List<GhostRankingSummary> rankings;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "GhostRankingSummary", description = "고스트 랭킹 요약")
    public static class GhostRankingSummary {
        @Schema(description = "고스트 랭킹 ID", example = "1")
        private Long rankingId;

        @Schema(description = "연결된 러닝 기록 ID", example = "25")
        private Long recordId;

        @Schema(description = "순위", example = "1")
        private Integer rankNo;

        @Schema(description = "랭커 닉네임", example = "runnerA")
        private String nickname;

        @Schema(description = "평균 페이스(분/km)", example = "4.52")
        private Double avgPace;

        @Schema(description = "총 거리(km)", example = "5.13")
        private Double distanceKm;

        @Schema(description = "고스트 코스 시작 위도", example = "37.5123")
        private Double startLat;

        @Schema(description = "고스트 코스 시작 경도", example = "127.1023")
        private Double startLng;

        public static GhostRankingSummary from(GhostRanking ranking) {
            RunningRecord record = ranking.getRunningRecord();
            return GhostRankingSummary.builder()
                    .rankingId(ranking.getId())
                    .recordId(record.getId())
                    .rankNo(ranking.getRankNo())
                    .nickname(record.getMember().getProfile().getNickname())
                    .avgPace(ranking.getAvgPace())
                    .distanceKm(UnitUtils.metersToKm(record.getTotalDistance()))
                    .startLat(ranking.getStartLat())
                    .startLng(ranking.getStartLng())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "GhostRankingDetailResponse", description = "고스트 기록 상세 응답")
    public static class GhostRankingDetailResponse {
        @Schema(description = "고스트 랭킹 ID", example = "1")
        private Long rankingId;

        @Schema(description = "연결된 러닝 기록 ID", example = "25")
        private Long recordId;

        @Schema(description = "순위", example = "1")
        private Integer rankNo;

        @Schema(description = "랭커 닉네임", example = "runnerA")
        private String nickname;

        @Schema(description = "주소 동", example = "잠실동")
        private String addressDong;

        @Schema(description = "거리 부문", example = "5K")
        private GhostCategory category;

        @Schema(description = "평균 페이스(분/km)", example = "4.52")
        private Double avgPace;

        @Schema(description = "총 거리(km)", example = "5.13")
        private Double distanceKm;

        @Schema(description = "고스트 코스 시작 위도", example = "37.5123")
        private Double startLat;

        @Schema(description = "고스트 코스 시작 경도", example = "127.1023")
        private Double startLng;

        @Schema(description = "지도에 표시할 고스트 코스 경로 좌표 리스트")
        private List<LatLng> path;

        public static GhostRankingDetailResponse from(GhostRanking ranking) {
            RunningRecord record = ranking.getRunningRecord();
            return GhostRankingDetailResponse.builder()
                    .rankingId(ranking.getId())
                    .recordId(record.getId())
                    .rankNo(ranking.getRankNo())
                    .nickname(record.getMember().getProfile().getNickname())
                    .addressDong(ranking.getAddressDong())
                    .category(ranking.getCategory())
                    .avgPace(ranking.getAvgPace())
                    .distanceKm(UnitUtils.metersToKm(record.getTotalDistance()))
                    .startLat(ranking.getStartLat())
                    .startLng(ranking.getStartLng())
                    .path(GeometryUtils.toLatLngList(record.getPath()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "GhostRunStartResponse", description = "고스트런 시작 응답")
    public static class GhostRunStartResponse {
        @Schema(description = "생성된 러닝 ID", example = "77")
        private Long runId;

        @Schema(description = "도전할 고스트 랭킹 ID", example = "1")
        private Long ghostRankingId;

        @Schema(description = "도전 대상 러닝 기록 ID", example = "25")
        private Long targetRecordId;

        @Schema(description = "도전 대상 닉네임", example = "runnerA")
        private String targetNickname;

        @Schema(description = "도전 대상 평균 페이스(분/km)", example = "4.52")
        private Double targetAvgPace;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "GhostRunFinishResponse", description = "고스트런 종료 응답")
    public static class GhostRunFinishResponse {
        @Schema(description = "러닝 ID", example = "77")
        private Long runId;

        @Schema(description = "고스트런 결과", example = "WIN", allowableValues = {"WIN", "LOSE"})
        private String result;

        @Schema(description = "내 평균 페이스(분/km)", example = "4.48")
        private Double myAvgPace;

        @Schema(description = "도전 대상 평균 페이스(분/km)", example = "4.52")
        private Double targetAvgPace;

        @Schema(description = "페이스 차이. 음수면 내가 더 빠름", example = "-0.04")
        private Double paceDiff;

        @Schema(description = "랭킹 갱신 여부", example = "true")
        private Boolean rankingUpdated;
    }
}
