package com.running.runapp.domain.groupRunning.dto;

import com.running.runapp.domain.groupRunning.domain.GroupRunning;

import java.time.LocalDateTime;

public class GroupResponse {

    public record GroupSummary(
            Long groupId,
            String title,
            String hostNickname,
            int currentParticipants,
            int maxParticipants,
            LocalDateTime startTime,
            String status,
            LocalDateTime createdAt // 최신순 정렬 확인용
    ) {
        // Entity -> DTO 변환 메서드
        public static GroupSummary from(GroupRunning group) {
            return new GroupSummary(
                    group.getId(),
                    group.getTitle(),
                    group.getHost().getNickname(),
                    group.getParticipants().size(),
                    group.getMaxParticipants(),
                    group.getStartTime(),
                    group.getStatus().name(),
                    group.getCreatedAt()
            );
        }
    }
}
