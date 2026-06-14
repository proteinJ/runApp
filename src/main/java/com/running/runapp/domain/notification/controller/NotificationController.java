package com.running.runapp.domain.notification.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.notification.dto.NotificationResponse;
import com.running.runapp.domain.notification.service.NotificationService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification", description = "알림 조회 및 읽음 처리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "내 알림 목록 조회",
            description = "점령 탈환, 팔로우 신청, 팔로우 수락 등 현재 로그인한 회원에게 도착한 알림을 최신순으로 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse.Info>>> getNotifications(
            @LoginMember Member me
    ) {
        List<NotificationResponse.Info> notifications = notificationService.getNotifications(me.getId());
        return ResponseEntity.ok(ApiResponse.success("알림 목록 조회 성공", notifications));
    }

    @Operation(summary = "읽지 않은 알림 개수 조회", description = "현재 로그인한 회원의 읽지 않은 알림 개수를 조회합니다.")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<NotificationResponse.UnreadCount>> getUnreadCount(
            @LoginMember Member me
    ) {
        NotificationResponse.UnreadCount unreadCount = notificationService.getUnreadCount(me.getId());
        return ResponseEntity.ok(ApiResponse.success("읽지 않은 알림 개수 조회 성공", unreadCount));
    }

    @Operation(summary = "전체 알림 읽음 처리", description = "현재 로그인한 회원의 읽지 않은 알림을 모두 읽음 처리합니다.")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @LoginMember Member me
    ) {
        notificationService.markAllAsRead(me.getId());
        return ResponseEntity.ok(ApiResponse.success("전체 알림 읽음 처리 성공"));
    }

    @Operation(summary = "알림 읽음 처리", description = "알림 ID로 단일 알림을 읽음 처리합니다. 본인 알림만 처리할 수 있습니다.")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @LoginMember Member me,
            @Parameter(description = "읽음 처리할 알림 ID", example = "1")
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(me.getId(), notificationId);
        return ResponseEntity.ok(ApiResponse.success("알림 읽음 처리 성공"));
    }
}
