package com.running.runapp.domain.profile.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "follows")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "follow_id")
    private String id;

    // 팔로우 하는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Profile follower;

    // 팔로우 받는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private Profile following;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowStatus status; // PENDING, ACCEPTED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum FollowStatus {
        PENDING, ACCEPTED
    }

    // 친구 신청 생성
    public static Follow request(Profile follower, Profile following) {
        Follow f = new Follow();
        f.follower = follower;
        f.following = following;
        f.status = FollowStatus.PENDING;
        f.createdAt = LocalDateTime.now();
        f.updatedAt = LocalDateTime.now();
        return f;
    }

    // 수락
    public void accept() {
        this.status = FollowStatus.ACCEPTED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.status == FollowStatus.PENDING;
    }
}