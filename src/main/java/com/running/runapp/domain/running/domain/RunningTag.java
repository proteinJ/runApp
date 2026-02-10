package com.running.runapp.domain.running.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "running_tag") // DB 테이블 이름
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 1, 2, 3... 번호표 자동 발급
    @Column(name = "tag_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagCategory category; // 1. 대분류 (시간, 레벨, 스타일 등)

    @Column(nullable = false, unique = true)
    private String name; // 2. 태그 이름 (새벽러닝, 런린이 등)

    @Column(name = "emoji")
    private String emoji; // 3. 귀여운 이모지 (🌅, 🐣)

    // 태그의 대분류 정의 (파일 안에 포함)
    public enum TagCategory {
        TIME,       // 시간대 (새벽, 밤)
        LEVEL,      // 수준 (초보, 고수)
        LOCATION,   // 장소 (한강, 트랙)
        GOAL,       // 목표 (다이어트, 기록)
        STYLE       // 러닝 성향 (스피드광, 장거리, 펀런)
    }
}