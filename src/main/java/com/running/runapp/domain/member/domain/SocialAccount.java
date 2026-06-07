package com.running.runapp.domain.member.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "social_account",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_social_provider_user", columnNames = {"provider", "provider_user_id"})
        },
        indexes = {
                @Index(name = "idx_social_member", columnList = "member_id")
        }
)
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_account_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId;

    @Column(length = 150)
    private String email;

    @Column(length = 100)
    private String nickname;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static SocialAccount link(
            Member member,
            Provider provider,
            String providerUserId,
            String email,
            String nickname,
            String profileImageUrl
    ) {
        return SocialAccount.builder()
                .member(member)
                .provider(provider)
                .providerUserId(providerUserId)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}