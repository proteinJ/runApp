package com.running.runapp.domain.spot.domain;

import com.running.runapp.domain.spot.dto.SpotRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point; // ⚠️ 꼭 이걸로 import 하세요!

import java.util.Optional;

@Entity
@Table(name = "spot") // 테이블 이름 소문자 (권장)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicUpdate
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 랜덤 문자열 ID(UUID) 인데 다시 IDENTITY로 바꿈
    @Column(name = "spot_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 장소 이름 (예: 반포대교 무지개분수)

    // 📍 핵심: PostGIS 위치 데이터 (지구 곡면 계산용)
    @Column(columnDefinition = "geography(Point, 4326)")
    private Point location;

    @Builder.Default
    @Column(name = "reward_amount", nullable = false, columnDefinition = "integer default 0")
    @Min(0)
    @Max(10000)
    private Integer rewardAmount = 0; // 방문 시 줄 보상 포인트

    @Column(columnDefinition = "TEXT") // 긴 설명도 가능하게
    private String description;

    @Column(name = "image_url") // 자바는 imageUrl, DB는 image_url
    private String imageUrl;

    // (선택) 프론트엔드에 주기 편하게 위도/경도 필드를 따로 둘 수도 있음
    private Double latitude;
    private Double longitude;

    public void update(SpotRequest.SpotUpdateRequest dto, Point newLocation) {
        final GeometryFactory geometryFactory;

        if (newLocation != null) {
            this.location = newLocation;
            this.latitude = dto.latitude();
            this.longitude = dto.longitude();
        }

        // dto.key 값이 null이 아닌 값이 존재한다면 업데이트
        Optional.ofNullable(dto.name()).ifPresent(name -> this.name = name);
        Optional.ofNullable(dto.description()).ifPresent(desc -> this.description = desc);
        Optional.ofNullable(dto.rewardAmount()).ifPresent(reward -> this.rewardAmount = reward);
    }
}