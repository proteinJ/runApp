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

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    private String city;
    private String street;

    @Column(name = "image_url")
    private String imageUrl;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "total_point", nullable = false)
    private Integer totalPoint = 0;

    @Column(name = "core_color_code", length = 50)
    private String coreColorCode;

    @Column(name = "equipped_title_name", length = 100)
    private String equippedTitleName;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Profile profile;

    public void updatePassword(String password) {
        this.password = password;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
        profile.setMember(this);
    }

    public void addPointAmount(int delta) {
        int current = (this.totalPoint == null) ? 0 : this.totalPoint;
        this.totalPoint = current + delta;
        if (this.totalPoint < 0) this.totalPoint = 0;
    }

    public void setCoreColorCode(String coreColorCode) {
        this.coreColorCode = coreColorCode;
    }

    public void setEquippedTitleName(String equippedTitleName) {
        this.equippedTitleName = equippedTitleName;
    }
}