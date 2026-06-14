package com.running.runapp.domain.notification.domain;

import com.running.runapp.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_member_id", nullable = false)
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_member_id")
    private Member actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 50)
    private TargetType targetType;

    @Column(name = "target_id", length = 100)
    private String targetId;

    @Column(name = "is_read", nullable = false, columnDefinition = "boolean default false")
    private boolean readStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public enum NotificationType {
        SPOT_STOLEN,
        FOLLOW_REQUEST,
        FOLLOW_ACCEPTED
    }

    public enum TargetType {
        SPOT,
        FOLLOW,
        PROFILE
    }

    public static Notification create(
            Member receiver,
            Member actor,
            NotificationType type,
            String title,
            String message,
            TargetType targetType,
            String targetId
    ) {
        Notification notification = new Notification();
        notification.receiver = receiver;
        notification.actor = actor;
        notification.type = type;
        notification.title = title;
        notification.message = message;
        notification.targetType = targetType;
        notification.targetId = targetId;
        notification.readStatus = false;
        notification.createdAt = LocalDateTime.now();
        return notification;
    }

    public void markAsRead() {
        if (readStatus) {
            return;
        }
        this.readStatus = true;
        this.readAt = LocalDateTime.now();
    }
}
