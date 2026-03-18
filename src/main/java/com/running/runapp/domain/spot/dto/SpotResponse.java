package com.running.runapp.domain.spot.dto;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.point.PointHistory;
import com.running.runapp.domain.spot.domain.Spot;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;


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


    @Builder
    public record SpotCheckinResponse(
            String spotName,
            Integer earnedPoints,
            Integer currentTotalPoints,
            Long visitLogId
    ) {
        public static SpotCheckinResponse of(Spot spot, PointHistory pointHistory, Member member) {
            return SpotCheckinResponse.builder()
                .spotName(spot.getName())
                .earnedPoints(spot.getRewardAmount())
                .currentTotalPoints(member.getTotalPoint())
                .visitLogId(pointHistory.getId())
                .build();
        }
    }
}
