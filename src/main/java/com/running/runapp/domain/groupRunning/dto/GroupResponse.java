package com.running.runapp.domain.groupRunning.dto;

import com.running.runapp.domain.groupRunning.domain.GroupRunning;

import java.time.LocalDateTime;
import java.util.List;

public class GroupResponse {


    // 1. 그룹러닝 요약 목록 조회용
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
                    group.getHost().getProfile().getNickname(),
                    group.getParticipants().size(),
                    group.getMaxParticipants(),
                    group.getStartTime(),
                    group.getDynamicStatus().name(), // 동적으로 바뀌게
                    group.getCreatedAt()
            );
        }
    }

    // 2. 상세 조회 결과용
    public record GroupDetail(
            Long groupId,
            String title,
            String hostNickname,
            String status,
            // 필요하다면 참여자 명단이나 러닝 코스 정보 등을 추가
            List<String> participantNicknames
    ) {
        public static GroupDetail from(GroupRunning group) {
            return new GroupDetail(
                    group.getId(),
                    group.getTitle(),
                    group.getHost().getProfile().getNickname(),
                    group.getDynamicStatus().name(),
                    group.getParticipants().stream()
                            .map(p -> p.getMember().getProfile().getNickname())
                            .toList()
            );
        }
    }
}
