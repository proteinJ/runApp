package com.running.runapp.domain.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.running.runapp.domain.member.domain.Follow;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    private String realname;

    private String city;
    private String street;

    @Column(name = "image_url")
    private String imageUrl;

    // 관리자 페이지에서 수정 시 사용하는 필드들
    private String description; // 👈 추가: AdminService에서 사용

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point lastLocation;

    private Double avg_pace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    @Column(name = "reward_amount", nullable = false, columnDefinition = "integer default 0")
    @Min(0)
    @Max(10000)
    private Integer totalPoint = 0;

    // ================== 비즈니스 로직 메서드 (수정용) ==================

    /**
     * 기본 프로필 수정
     */
    public void updateProfile(String nickname, String realname) {
        this.nickname = nickname;
        this.realname = realname;
    }

    /**
     * 비밀번호 수정
     */
    public void updatePassword(String password) {
        this.password = password;
    }

    /**
     * 관리자 전용: 상세 정보 업데이트
     */
    public void updateAdminInfo(String nickname, String description, Integer rewardAmount, String imageUrl, Point location) {
        this.nickname = nickname;
        this.description = description;
        this.totalPoint = rewardAmount;
        this.imageUrl = imageUrl;
        this.lastLocation = location;
    }

    /**
     * 포인트 적립
     */
    public Integer addPointAmount(int earnedPoints) {
        return this.totalPoint += earnedPoints;
    }

    @OneToMany(mappedBy = "follower")
    @Builder.Default
    @JsonIgnore
    private List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "following")
    @Builder.Default
    @JsonIgnore
    private List<Follow> followers = new ArrayList<>();
}