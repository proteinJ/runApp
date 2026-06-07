package com.running.runapp.domain.member.domain;

import com.running.runapp.domain.profile.domain.Profile;
import jakarta.persistence.*;
import lombok.*;

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

    private String realname;

    private String city;
    private String street;

    @Column(name = "image_url")
    private String imageUrl;

    // 관리자 페이지에서 수정 시 사용하는 필드들
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ================== 비즈니스 로직 메서드 (수정용) ==================

    /**
     * 비밀번호 수정
     */
    public void updatePassword(String password) {
        this.password = password;
    }


    // FK
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Profile profile;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<SocialAccount> socialAccounts = new java.util.ArrayList<>();

    // Member와 Profile 양방향 연결 메서드
    public void setProfile(Profile profile) {
        this.profile = profile;
        profile.setMember(this);
    }
}