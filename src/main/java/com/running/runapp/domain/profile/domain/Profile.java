package com.running.runapp.domain.profile.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.running.runapp.domain.member.domain.Member;
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


    /**
     * 칭호 장착 메서드
     */
    public void equipTitle(Title title) {
        this.equippedTitle = title;
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


}