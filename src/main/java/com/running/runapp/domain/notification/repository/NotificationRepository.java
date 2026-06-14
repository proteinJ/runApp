package com.running.runapp.domain.notification.repository;

import com.running.runapp.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiver_IdOrderByCreatedAtDesc(Long receiverId);

    long countByReceiver_IdAndReadStatusFalse(Long receiverId);

    Optional<Notification> findByIdAndReceiver_Id(Long notificationId, Long receiverId);

    @Modifying
    @Query("""
        update Notification n
        set n.readStatus = true,
            n.readAt = :readAt
        where n.receiver.id = :memberId
          and n.readStatus = false
    """)
    int markAllAsRead(@Param("memberId") Long memberId, @Param("readAt") LocalDateTime readAt);
}
