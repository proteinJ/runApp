package com.running.runapp.domain.profile.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Title {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "title_id")
    private Long id;

    private String name; // 칭호 이름 (예: "킹 오브 러너")

    @Column(name = "title_code")
    private String titleCode; // 프론트와 약속한 코드 (예: "TITLE_001_GOLD")

    @Enumerated(EnumType.STRING)
    private Rarity rarity; // 등급 (NORMAL, RARE, EPIC, LEGENDARY)

    @Column(name = "exp_bonus_ratio")
    private Double expBonusRatio; // 경험치 보너스 배수

    @Column(name = "point_bonus_ratio")
    private Double pointBonusRatio; // 포인트 보너스 배수

    private String description; // 획득 방법 설명


    public void updateInfo(String name, String titleCode, Rarity rarity,
                           Double expBonusRatio, Double pointBonusRatio, String description) {

        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (titleCode != null && !titleCode.isBlank()) {
            this.titleCode = titleCode;
        }
        if (rarity != null) {
            this.rarity = rarity;
        }

        // Double 타입의 null 체크 및 NaN(Not a Number) 방어 코드
        if (expBonusRatio != null && !expBonusRatio.isNaN()) {
            this.expBonusRatio = expBonusRatio;
        }
        if (pointBonusRatio != null && !pointBonusRatio.isNaN()) {
            this.pointBonusRatio = pointBonusRatio;
        }

        if (description != null && !description.isBlank()) {
            this.description = description;
        }
    }
}
