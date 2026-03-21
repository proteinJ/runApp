package com.running.runapp.domain.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public void updateProfile(String nickname, String realname) {
        this.nickname = nickname;
        this.realname = realname;
    }

    public void updatePassword(String password) { this.password = password; }

    // 내가 팔로우하는 사람들 목록 (내가 팬)
    // 의미: Follow 테이블의 'follower' 칸에 내 이름(ID)이 적힌 내역들 가져와!
    @OneToMany(mappedBy = "follower")
    @Builder.Default // 1. 빌더 써도 초기화 유지해줘!
    @JsonIgnore      // 2. JSON 만들 때 무한루프 끊어줘! (DTO 안 쓸 때 대비)
    private List<Follow> followings = new ArrayList<>();

    // 나를 팔로우하는 사람들 목록 (내가 스타)
    // 의미: Follow 테이블의 'following' 칸에 내 이름(ID)이 적힌 내역들 가져와!
    @OneToMany(mappedBy = "following")
    @Builder.Default // 1. 빌더 써도 초기화 유지해줘!
    @JsonIgnore      // 2. JSON 만들 때 무한루프 끊어줘! (DTO 안 쓸 때 대비)
    private List<Follow> followers = new ArrayList<>();

    public Integer addPointAmount(int earnedPoints) {
        return this.totalPoint += earnedPoints;
    }
}
