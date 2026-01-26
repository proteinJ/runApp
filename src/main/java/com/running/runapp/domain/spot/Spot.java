package com.running.runapp.domain.spot;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point; // ⚠️ 꼭 이걸로 import 하세요!

@Entity
@Table(name = "spot") // 테이블 이름 소문자 (권장)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // 랜덤 문자열 ID
    @Column(name = "spot_id")
    private String spotId;

    @Column(nullable = false, length = 100)
    private String name; // 장소 이름 (예: 반포대교 무지개분수)

    // 📍 핵심: PostGIS 위치 데이터 (지구 곡면 계산용)
    @Column(columnDefinition = "geography(Point, 4326)")
    private Point location;

    @Column(name = "reward_amount")
    private Integer rewardAmount; // 방문 시 줄 보상 포인트

    @Column(columnDefinition = "TEXT") // 긴 설명도 가능하게
    private String description;

    @Column(name = "image_url") // 자바는 imageUrl, DB는 image_url
    private String imageUrl;

    // (선택) 프론트엔드에 주기 편하게 위도/경도 필드를 따로 둘 수도 있음
    private Double latitude;
    private Double longitude;
}