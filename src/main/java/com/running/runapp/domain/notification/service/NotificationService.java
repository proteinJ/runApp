package com.running.runapp.domain.notification.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.notification.domain.Notification;
import com.running.runapp.domain.notification.dto.NotificationResponse;
import com.running.runapp.domain.notification.repository.NotificationRepository;
import com.running.runapp.domain.spot.domain.Spot;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createSpotStolen(Member previousOccupier, Member newOccupier, Spot spot) {
        if (previousOccupier == null || newOccupier == null || previousOccupier.getId().equals(newOccupier.getId())) {
            return;
        }

        String actorNickname = getNickname(newOccupier);
        notificationRepository.save(Notification.create(
                previousOccupier,
                newOccupier,
                Notification.NotificationType.SPOT_STOLEN,
                "영토를 빼앗겼어요!",
                "'" + actorNickname + "'님이 '" + spot.getName() + "' 스팟을 차지했습니다.",
                Notification.TargetType.SPOT,
                String.valueOf(spot.getId())
        ));
    }

    public void createFollowRequest(Member receiver, Member requester, String followId) {
        if (receiver == null || requester == null || receiver.getId().equals(requester.getId())) {
            return;
        }

        String actorNickname = getNickname(requester);
        notificationRepository.save(Notification.create(
                receiver,
                requester,
                Notification.NotificationType.FOLLOW_REQUEST,
                "팔로우 신청이 도착했어요",
                "'" + actorNickname + "'님이 팔로우를 신청했습니다.",
                Notification.TargetType.FOLLOW,
                followId
        ));
    }

    public void createFollowAccepted(Member receiver, Member accepter, String followId) {
        if (receiver == null || accepter == null || receiver.getId().equals(accepter.getId())) {
            return;
        }

        String actorNickname = getNickname(accepter);
        notificationRepository.save(Notification.create(
                receiver,
                accepter,
                Notification.NotificationType.FOLLOW_ACCEPTED,
                "팔로우 신청이 수락됐어요",
                "'" + actorNickname + "'님이 팔로우 신청을 수락했습니다.",
                Notification.TargetType.FOLLOW,
                followId
        ));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse.Info> getNotifications(Long memberId) {
        return notificationRepository.findByReceiver_IdOrderByCreatedAtDesc(memberId).stream()
                .map(NotificationResponse.Info::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationResponse.UnreadCount getUnreadCount(Long memberId) {
        return new NotificationResponse.UnreadCount(notificationRepository.countByReceiver_IdAndReadStatusFalse(memberId));
    }

    public void markAsRead(Long memberId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndReceiver_Id(notificationId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();
    }

    public void markAllAsRead(Long memberId) {
        notificationRepository.markAllAsRead(memberId, LocalDateTime.now());
    }

    private String getNickname(Member member) {
        if (member.getProfile() == null || member.getProfile().getNickname() == null) {
            return "알 수 없는 사용자";
        }
        return member.getProfile().getNickname();
    }
}
