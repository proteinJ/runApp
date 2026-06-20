package com.running.runapp.domain.groupRunning.dto;

import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "그룹 러닝 응답 DTO")
public class GroupResponse {

    @Schema(description = "그룹 러닝 목록 요약")
    public record GroupSummary(
            @Schema(description = "그룹 ID", example = "1")
            Long groupId,

            @Schema(description = "모집글 제목", example = "한강 저녁 러닝 같이 달려요!")
            String title,

            @Schema(description = "방장 닉네임", example = "달리기왕")
            String hostNickname,

            @Schema(description = "방장 회원 ID", example = "10")
            Long hostId,

            @Schema(description = "현재 참여 인원", example = "2")
            int currentParticipants,

            @Schema(description = "최대 참여 인원 (2~5)", example = "4")
            int maxParticipants,

            @Schema(description = "러닝 거리(km)", example = "5")
            Integer distance,

            @Schema(description = "러닝 시작 시간", example = "2026-06-10T19:00:00")
            LocalDateTime startTime,

            @Schema(description = "그룹 상태", example = "RECRUITING",
                    allowableValues = {"RECRUITING", "RUNNING", "COMPLETED", "CANCELED"})
            String status,

            @Schema(description = "모집글 생성 시간", example = "2026-06-05T12:00:00")
            LocalDateTime createdAt,

            @Schema(description = "내가 이 그룹에 참여 중인지 여부", example = "false")
            boolean isParticipating,

            @Schema(description = "러닝 장소명", example = "반포 한강공원")
            String location
    ) {
        public static GroupSummary from(GroupRunning group, Long memberId) {
            boolean isParticipating = group.getParticipants().stream()
                    .anyMatch(gm -> gm.getMember().getId().equals(memberId));
            return new GroupSummary(
                    group.getId(),
                    group.getTitle(),
                    group.getHost().getProfile().getNickname(),
                    group.getHost().getId(),
                    group.getParticipants().size(),
                    group.getMaxParticipants(),
                    group.getDistance(),
                    group.getStartTime(),
                    group.getDynamicStatus().name(),
                    group.getCreatedAt(),
                    isParticipating,
                    group.getLocation()
            );
        }
    }

    @Schema(description = "그룹 러닝 상세 정보")
    public record GroupDetail(
            @Schema(description = "그룹 ID", example = "1")
            Long groupId,

            @Schema(description = "모집글 제목", example = "한강 저녁 러닝 같이 달려요!")
            String title,

            @Schema(description = "방장 닉네임", example = "달리기왕")
            String hostNickname,

            @Schema(description = "방장 회원 ID", example = "10")
            Long hostId,

            @Schema(description = "모집글 내용", example = "페이스 6분대 환영, 반포대교 집합입니다.")
            String content,

            @Schema(description = "그룹 상태", example = "RECRUITING",
                    allowableValues = {"RECRUITING", "RUNNING", "COMPLETED", "CANCELED"})
            String status,

            @Schema(description = "현재 참여 인원", example = "2")
            int currentParticipants,

            @Schema(description = "최대 참여 인원 (2~5)", example = "4")
            int maxParticipants,

            @Schema(description = "러닝 거리(km)", example = "5")
            Integer distance,

            @Schema(description = "러닝 장소명", example = "반포 한강공원")
            String location,

            @Schema(description = "러닝 장소 주소", example = "서울특별시 서초구 반포동 1")
            String address,

            @Schema(description = "내가 이 그룹에 참여 중인지 여부", example = "true")
            boolean isParticipating,

            @Schema(description = "참여자 닉네임 목록", example = "[\"달리기왕\", \"마라토너\"]")
            List<String> participantNicknames,

            @Schema(description = "러닝 시작 시간", example = "2026-06-10T19:00:00")
            LocalDateTime startTime,

            @Schema(description = "러닝 종료 예정 시간", example = "2026-06-10T20:00:00")
            LocalDateTime endTime,

            @Schema(description = "모집글 생성 시간", example = "2026-06-05T12:00:00")
            LocalDateTime createdAt,

            @Schema(description = "러닝 장소 위도", example = "37.5133")
            Double lat,

            @Schema(description = "러닝 장소 경도", example = "126.9947")
            Double lon
    ) {
        public static GroupDetail from(GroupRunning group, Long memberId) {
            boolean isParticipating = group.getParticipants().stream()
                    .anyMatch(gm -> gm.getMember().getId().equals(memberId));
            return new GroupDetail(
                    group.getId(),
                    group.getTitle(),
                    group.getHost().getProfile().getNickname(),
                    group.getHost().getId(),
                    group.getContent(),
                    group.getDynamicStatus().name(),
                    group.getParticipants().size(),
                    group.getMaxParticipants(),
                    group.getDistance(),
                    group.getLocation(),
                    group.getAddress(),
                    isParticipating,
                    group.getParticipants().stream()
                            .map(p -> p.getMember().getProfile().getNickname())
                            .toList(),
                    group.getStartTime(),
                    group.getEndTime(),
                    group.getCreatedAt(),
                    group.getLat(),
                    group.getLon()
            );
        }
    }
}
