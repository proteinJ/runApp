package com.running.runapp.domain.spot.dto;

import com.running.runapp.domain.profile.dto.ProfileResponse;
import com.running.runapp.domain.spot.domain.Spot;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

import java.time.LocalDateTime;


public class SpotResponse {

    public record SummaryInfo (
            Long id,
            String name,
            @Min(0) @Max(10000)
            Integer rewardAmount,
            Double latitude,
            Double longitude,
            boolean canCheckIn
    ) {
        public static SummaryInfo from(Spot spot) {
            return new SummaryInfo(
                    spot.getId(),
                    spot.getName(),
                    spot.getRewardAmount(),
                    spot.getLocation().getY(),
                    spot.getLocation().getX(),
                    true
            );
        }
    }

    public record DetailInfo (
            Long id,
            String name,
            String description,

            @Min(0) @Max(10000)
            Integer rewardAmount,
            Double latitude,
            Double longitude
//            String imageUrl,
//            List<String> tags
    ) {}


    public record CooldownInfo(
            @Schema(description = "스팟 ID", example = "1")
            Long spotId,
            @Schema(description = "스팟 이름", example = "구서역")
            String spotName,
            @Schema(description = "마지막 체크인 시각")
            LocalDateTime lastCheckinAt,
            @Schema(description = "쿨타임 종료 시각 (lastCheckinAt + 24h)")
            LocalDateTime cooldownEndsAt,
            @Schema(description = "남은 초 (카운트다운용)", example = "72000")
            long remainingSeconds
    ) {}

    @Builder
    public record SpotCheckinResponse(
            @Schema(description = "스팟 이름", example = "해운대 해수욕장")
            String spotName,

            @Schema(description = "얻은 포인트", example = "10")
            Integer earnedPoints,

            @Schema(description = "현재 보유 포인트", example = "310")
            Integer currentTotalPoints,

            @Schema(description = "방문 기록 ID", example = "3")
            String visitLogId,

            @Schema(description = "얻은 경험치", example = "100")
            Long earnedExp,

            @Schema(description = "레벨업 유무(폭주 트리거용)", example = "true")
            boolean isLevelUp,

            @Schema(description = "현재 레벨", example = "15")
            int currentLevel,

            @Schema(description = "현재 총 경험치량(게이지 바 갱신용)", example = "300")
            long totalExp
    ) {
        public static SpotCheckinResponse of(Spot spot, Integer earnedPoint, Integer currentTotalPoints, String visitLogId, ProfileResponse.ExpRewardResult expResult) {
            return new SpotCheckinResponse(
                    spot.getName(),
                    earnedPoint,
                    currentTotalPoints,
                    visitLogId,
                    spot.getExpAmount(),
                    expResult.isLevelUp(),
                    expResult.currentLevel(),
                    expResult.totalExp()
            );
        }
    }
}
