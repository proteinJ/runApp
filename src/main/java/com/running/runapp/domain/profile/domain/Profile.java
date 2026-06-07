package com.running.runapp.domain.profile.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "last_location", columnDefinition = "geometry(Point, 4326)")
    private Point lastLocation;

    @Column(name = "avg_pace")
    private Double avgPace;

    @Column(name = "total_distance")
    private Double totalDistance = 0.0;

    @Column(name = "core_color_code", length = 50)
    @Builder.Default
    private String coreColorCode = "CORE_BLACK";

    @Builder.Default
    @Column(name = "total_point", nullable = false, columnDefinition = "integer default 0")
    @Min(0)
    @Max(10000)
    private Integer totalPoint = 0;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "follower")
    @Builder.Default
    @JsonIgnore
    private List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "following")
    @Builder.Default
    @JsonIgnore
    private List<Follow> followers = new ArrayList<>();

    // 현재 장착 중인 칭호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipped_title_id")
    private Title equippedTitle;

    // 내가 획득한 칭호 목록
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<ProfileTitle> ownedTitles = new ArrayList<>();

    @Column(nullable = false)
    @Min(value = 1, message = "최소 레벨은 1레벨 입니다.")
    @Max(value = 100, message = "초대 레벨은 100레벨 입니다.")
    private int level = 1;

    @Column(nullable = false)
    private long totalExp = 0L;


    /**
     * 칭호 장착 메서드
     */
    public void equipTitle(ProfileTitle profileTitle) {
        if (!this.equals(profileTitle.getProfile())) {
            throw new BusinessException(ErrorCode.NOT_YOUR_TITLE);
        }

        this.equippedTitle = profileTitle.getTitle();
    }

    /**
     * 포인트 적립
     */
    public Integer addPointAmount(int earnedPoints) {
        return this.totalPoint += earnedPoints;
    }

    /**
     * 기본 프로필 수정
     */
    private void updateProfile(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 관리자 전용: 상세 정보 업데이트
     */
    private void updateAdminInfo(String nickname, String description, Integer rewardAmount, String imageUrl, Point location) {
        this.nickname = nickname;
//        this.description = description;
        this.totalPoint = rewardAmount;
//        this.imageUrl = imageUrl;
        this.lastLocation = location;
    }


    public void updateRunStats(double addedDistanceMeter, Double runPace, long totalFinishedRuns) {
        this.totalDistance = (this.totalDistance == null ? 0.0 : this.totalDistance) + addedDistanceMeter;
        if (runPace != null && runPace > 0 && totalFinishedRuns > 0) {
            double prevSum = (this.avgPace == null || this.avgPace == 0.0 ? 0.0 : this.avgPace) * (totalFinishedRuns - 1);
            this.avgPace = (prevSum + runPace) / totalFinishedRuns;
        }
    }

    // 경험치 증가 메서드
    public Long addExp(Long gainedExp) {
        if (gainedExp == null || gainedExp <= 0) return this.totalExp;
        return this.totalExp += gainedExp;
    }

    // 레벨 갱신 메서드
    public boolean updateLevel(int finalLevel) {
        if (this.level < finalLevel) {
            this.level = finalLevel;
            return true; // 레벨업 성공!
        }
        return false;
    }

    public String getCoreColorCode() {
        return coreColorCode;
    }

    public void setCoreColorCode(String coreColorCode) {
        this.coreColorCode = coreColorCode;
    }
}