package com.running.runapp.domain.profile.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfileTitle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "title_id")
    private Title title;

    private LocalDateTime acquiredAt; // 획득 날짜 및 시각

    public static ProfileTitle grantTitle(Profile profile, Title title) {
        return ProfileTitle.builder()
                .profile(profile)
                .title(title)
                .acquiredAt(LocalDateTime.now())
                .build();
    }
}
