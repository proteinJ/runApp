package com.running.runapp.domain.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

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
    private String imageu_url;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point lastLocation;

    private Double avg_pace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public void updateProfile(String nickname, String realname) {
        this.nickname = nickname;
        this.realname = realname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
