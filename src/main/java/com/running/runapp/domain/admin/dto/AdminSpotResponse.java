package com.running.runapp.domain.admin.dto;

import com.running.runapp.domain.spot.domain.Spot;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminSpotResponse {

    @Getter
    @Builder
    public static class Summary {
        private Long spotId;
        private String name;
        private Integer rewardAmount;
        private Double latitude;
        private Double longitude;

        public static Summary from(Spot s) {
            return Summary.builder()
                    .spotId(s.getId())
                    .name(s.getName())
                    .rewardAmount(s.getRewardAmount())
                    .latitude(s.getLatitude())
                    .longitude(s.getLongitude())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Detail {
        private Long spotId;
        private String name;
        private String description;
        private String imageUrl;
        private Integer rewardAmount;
        private Double latitude;
        private Double longitude;

        public static Detail from(Spot s) {
            return Detail.builder()
                    .spotId(s.getId())
                    .name(s.getName())
                    .description(s.getDescription())
                    .imageUrl(s.getImageUrl())
                    .rewardAmount(s.getRewardAmount())
                    .latitude(s.getLatitude())
                    .longitude(s.getLongitude())
                    .build();
        }
    }
}