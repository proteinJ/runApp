package com.running.runapp.domain.spot.dto;

import com.running.runapp.domain.profile.dto.ProfileResponse;
import com.running.runapp.domain.spot.domain.Spot;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

import java.time.LocalDateTime;


public class SpotResponse {

    @Schema(name = "SpotSummaryInfo", description = "주변 스팟 요약 응답")
    public record SummaryInfo (
            @Schema(description = "스팟 ID", example = "32")
            Long id,
            @Schema(description = "스팟 이름", example = "구서역 GS 편의점")
            String name,
            @Schema(description = "체크인 기본 보상 포인트", example = "100")
            @Min(0) @Max(10000)
            Integer rewardAmount,
            @Schema(description = "스팟 위도", example = "35.246509")
            Double latitude,
            @Schema(description = "스팟 경도", example = "129.091786")
            Double longitude,
            @Schema(description = "현재 로그인 회원의 체크인 가능 여부", example = "true")
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

    @Schema(name = "SpotDetailInfo", description = "스팟 상세 및 현재 점령 정보 응답")
    public record DetailInfo (
            @Schema(description = "스팟 ID", example = "32")
            Long id,
            @Schema(description = "스팟 이름", example = "구서역 GS 편의점")
            String name,
            @Schema(description = "스팟 설명", example = "구서역 근처 체크인 스팟")
            String description,

            @Schema(description = "체크인 기본 보상 포인트", example = "100")
            @Min(0) @Max(10000)
            Integer rewardAmount,
            @Schema(description = "스팟 위도", example = "35.246509")
            Double latitude,
            @Schema(description = "스팟 경도", example = "129.091786")
            Double longitude,
            @Schema(description = "현재 점령자 회원 ID. 점령자가 없으면 null", example = "1")
            Long occupierMemberId,
            @Schema(description = "현재 점령자의 해당 스팟 누적 체크인 수", example = "3")
            Integer occupierCheckinCount,
            @Schema(description = "현재 로그인 회원의 해당 스팟 누적 체크인 수", example = "2")
            Long myCheckinCount
//            String imageUrl,
//            List<String> tags
    ) {}

    @Schema(name = "OccupiedSpotInfo", description = "점령된 스팟 정보 응답")
    public record OccupiedSpotInfo(
            @Schema(description = "스팟 ID", example = "1")
            Long spotId,
            @Schema(description = "스팟 이름", example = "구서 이마트 앞 광장")
            String name,
            @Schema(description = "스팟 위도", example = "35.2486")
            Double latitude,
            @Schema(description = "스팟 경도", example = "129.0921")
            Double longitude,
            @Schema(description = "점령자 회원 ID", example = "5")
            Long occupierMemberId,
            @Schema(description = "점령자 닉네임", example = "날쌘돌이")
            String occupierNickname,
            @Schema(description = "점령/탈환 발생 시각. 기존 데이터는 null일 수 있습니다.", example = "2026-06-14T10:40:00")
            LocalDateTime occupiedAt
    ) {
        public static OccupiedSpotInfo from(Spot spot) {
            return new OccupiedSpotInfo(
                    spot.getId(),
                    spot.getName(),
                    spot.getLatitude(),
                    spot.getLongitude(),
                    spot.getOccupier().getId(),
                    spot.getOccupier().getProfile() == null ? null : spot.getOccupier().getProfile().getNickname(),
                    spot.getOccupiedAt()
            );
        }
    }


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
            long remainingSeconds,
            @Schema(description = "체크인 시 획득 포인트", example = "10")
            Integer rewardAmount,
            @Schema(description = "체크인 시 획득 경험치", example = "100")
            Long expAmount
    ) {}

    @Builder
    @Schema(name = "SpotCheckinResponse", description = "스팟 체크인 및 점령/탈환 결과 응답")
    public record SpotCheckinResponse(
            @Schema(description = "스팟 이름", example = "해운대 해수욕장")
            String spotName,

            @Schema(description = "체크인으로 얻은 기본 포인트. 점령/탈환 보너스는 occupationBonusPoints에 별도 표시됩니다.", example = "100")
            Integer earnedPoints,

            @Schema(description = "체크인 기본 포인트와 점령/탈환 보너스까지 반영된 현재 보유 포인트", example = "300")
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
            long totalExp,

            @Schema(description = "이번 체크인으로 점령자 변경 및 보너스 지급이 발생했는지 여부", example = "true")
            boolean occupationChanged,

            @Schema(
                    description = "점령 이벤트 타입. SPOT_OCCUPY는 첫 점령, SPOT_STEAL은 탈환, 변경 없음은 null",
                    example = "SPOT_STEAL",
                    allowableValues = {"SPOT_OCCUPY", "SPOT_STEAL"}
            )
            String occupationType,

            @Schema(description = "점령/탈환 보너스 포인트. 첫 점령은 50, 탈환은 100, 변경 없음은 0", example = "100")
            Integer occupationBonusPoints,

            @Schema(description = "체크인 처리 후 현재 점령자 회원 ID. 점령자가 없으면 null", example = "2")
            Long occupierMemberId,

            @Schema(description = "체크인 처리 후 현재 점령자의 해당 스팟 누적 체크인 수", example = "4")
            Integer occupierCheckinCount
    ) {
        public static SpotCheckinResponse of(Spot spot, Integer earnedPoint, Integer currentTotalPoints, String visitLogId,
                                             ProfileResponse.ExpRewardResult expResult, OccupationResult occupationResult) {
            return new SpotCheckinResponse(
                    spot.getName(),
                    earnedPoint,
                    currentTotalPoints,
                    visitLogId,
                    spot.getExpAmount(),
                    expResult.isLevelUp(),
                    expResult.currentLevel(),
                    expResult.totalExp(),
                    occupationResult.changed(),
                    occupationResult.type(),
                    occupationResult.bonusPoints(),
                    occupationResult.occupierMemberId(),
                    occupationResult.occupierCheckinCount()
            );
        }
    }

    @Schema(name = "OccupationResult", description = "점령/탈환 내부 계산 결과")
    public record OccupationResult(
            @Schema(description = "점령자 변경 여부", example = "true")
            boolean changed,
            @Schema(description = "점령 이벤트 타입", example = "SPOT_STEAL")
            String type,
            @Schema(description = "지급된 보너스 포인트", example = "100")
            Integer bonusPoints,
            @Schema(description = "현재 점령자 회원 ID", example = "2")
            Long occupierMemberId,
            @Schema(description = "현재 점령자의 누적 체크인 수", example = "4")
            Integer occupierCheckinCount
    ) {
        public static OccupationResult unchanged(Spot spot) {
            Long occupierMemberId = spot.getOccupier() == null ? null : spot.getOccupier().getId();
            return new OccupationResult(
                    false,
                    null,
                    0,
                    occupierMemberId,
                    spot.getOccupierCheckinCount()
            );
        }

        public static OccupationResult changed(String type, Integer bonusPoints, Spot spot) {
            Long occupierMemberId = spot.getOccupier() == null ? null : spot.getOccupier().getId();
            return new OccupationResult(
                    true,
                    type,
                    bonusPoints,
                    occupierMemberId,
                    spot.getOccupierCheckinCount()
            );
        }
    }
}
