package com.running.runapp.domain.groupRunning.dto;

import com.running.runapp.domain.groupRunning.domain.GroupRunning;

import java.time.LocalDateTime;

public class GroupResponse {

    public record GroupSummary(
            Long groupId,
            String title,
            String hostNickname,       // 방장 이름 (Member 엔티티에서 추출)
            int currentParticipants,   // 현재 참여 인원 수 (participants.size())
            int maxParticipants,       // 최대 모집 인원
            LocalDateTime startTime,
            String status              // RECRUITING 등 상태
    ) {
        
        // 엔티티 -> DTO로 변환하는 정적 팩토리 메서드
        public static GroupSummary from(GroupRunning group){
            return new GroupSummary(
                    group.getId(),
                    group.getTitle(),
                    group.getHost().getNickname(),
                    group.getParticipants().size(),
                    group.getMaxParticipants(),
                    group.getStartTime(),
                    group.getStatus().name()
            );
        }
    }


}
