package com.running.runapp.domain.notification.dto;

import com.running.runapp.domain.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class NotificationResponse {

    @Schema(name = "NotificationInfo", description = "알림 정보")
    public record Info(
            @Schema(description = "알림 ID", example = "1")
            Long notificationId,
            @Schema(description = "알림 타입", example = "SPOT_STOLEN", allowableValues = {"SPOT_STOLEN", "FOLLOW_REQUEST", "FOLLOW_ACCEPTED"})
            String type,
            @Schema(description = "알림 제목", example = "영토를 빼앗겼어요!")
            String title,
            @Schema(description = "알림 내용", example = "'날쌘돌이'님이 '구서역 GS 편의점' 스팟을 차지했습니다.")
            String message,
            @Schema(description = "연결 대상 타입", example = "SPOT", allowableValues = {"SPOT", "FOLLOW", "PROFILE"})
            String targetType,
            @Schema(description = "연결 대상 ID. SPOT이면 spotId, FOLLOW면 followId", example = "32")
            String targetId,
            @Schema(description = "알림을 발생시킨 회원 ID", example = "5")
            Long actorMemberId,
            @Schema(description = "알림을 발생시킨 회원 닉네임", example = "날쌘돌이")
            String actorNickname,
            @Schema(description = "읽음 여부", example = "false")
            boolean read,
            @Schema(description = "알림 생성 시각", example = "2026-06-14T10:40:00")
            LocalDateTime createdAt,
            @Schema(description = "읽은 시각. 읽지 않았으면 null", example = "2026-06-14T10:45:00")
            LocalDateTime readAt
    ) {
        public static Info from(Notification notification) {
            return new Info(
                    notification.getId(),
                    notification.getType().name(),
                    notification.getTitle(),
                    notification.getMessage(),
                    notification.getTargetType().name(),
                    notification.getTargetId(),
                    notification.getActor() == null ? null : notification.getActor().getId(),
                    notification.getActor() == null || notification.getActor().getProfile() == null
                            ? null
                            : notification.getActor().getProfile().getNickname(),
                    notification.isReadStatus(),
                    notification.getCreatedAt(),
                    notification.getReadAt()
            );
        }
    }

    @Schema(name = "NotificationUnreadCount", description = "읽지 않은 알림 개수")
    public record UnreadCount(
            @Schema(description = "읽지 않은 알림 개수", example = "3")
            long unreadCount
    ) {}
}
