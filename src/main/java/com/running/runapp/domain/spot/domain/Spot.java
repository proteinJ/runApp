package com.running.runapp.domain.spot.domain;

import com.running.runapp.domain.spot.dto.SpotRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.locationtech.jts.geom.Point;

import java.util.Optional;

@Entity
@Table(name = "spot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicUpdate
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spot_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point location;

    @Builder.Default
    @Column(name = "reward_amount", nullable = false, columnDefinition = "integer default 0")
    @Min(0)
    @Max(10000)
    private Integer rewardAmount = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    private Double latitude;
    private Double longitude;

    public void updateSpotInfo(String name, String description, Integer rewardAmount,
                               String imageUrl, Point location, Double latitude, Double longitude) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (rewardAmount != null) this.rewardAmount = rewardAmount;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (location != null) {
            this.location = location;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    // 기존의 DTO 기반 업데이트 메서드 (필요시 유지)
    public void update(SpotRequest.SpotUpdateRequest dto, Point newLocation) {
        if (newLocation != null) {
            this.location = newLocation;
            this.latitude = dto.latitude();
            this.longitude = dto.longitude();
        }
        Optional.ofNullable(dto.name()).ifPresent(name -> this.name = name);
        Optional.ofNullable(dto.description()).ifPresent(desc -> this.description = desc);
        Optional.ofNullable(dto.rewardAmount()).ifPresent(reward -> this.rewardAmount = reward);
    }
}