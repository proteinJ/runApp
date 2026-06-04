package com.running.runapp.domain.groupRunning.dto;

import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import com.running.runapp.domain.groupRunning.domain.GroupStatus;
import com.running.runapp.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "그룹 러닝 요청 DTO")
public class GroupRequest {

    @Schema(description = "그룹 러닝 생성 요청")
    public record groupAdd(
            @Schema(description = "모집글 제목", example = "한강 저녁 러닝 같이 달려요!")
            String title,

            @Schema(description = "모집글 내용", example = "페이스 6분대 환영, 반포대교 집합입니다.")
            String content,

            @Schema(description = "최대 참여 인원 (2~5)", example = "4")
            Integer maxParticipants,

            @Schema(description = "러닝 시작 시간", example = "2026-06-10T19:00:00")
            LocalDateTime startTime,

            @Schema(description = "러닝 종료 예정 시간", example = "2026-06-10T20:00:00")
            LocalDateTime endTime,

            @Schema(description = "러닝 목표 거리 (km)", example = "5")
            Integer distance,

            @Schema(description = "러닝 장소명", example = "반포 한강공원")
            String location,

            @Schema(description = "러닝 장소 주소", example = "서울특별시 서초구 반포동 1")
            String address
    ) {
        public GroupRunning toEntity(Member host) {
            return GroupRunning.builder()
                    .title(this.title())
                    .content(this.content())
                    .maxParticipants(this.maxParticipants())
                    .startTime(this.startTime())
                    .endTime(this.endTime())
                    .status(GroupStatus.RECRUITING)
                    .host(host)
                    .location(this.location())
                    .address(this.address())
                    .distance(this.distance())
                    .build();
        }
    }

    @Schema(description = "그룹 러닝 정보 수정 요청 (null 필드는 변경하지 않음)")
    public record UpdateExtraRequest(
            @Schema(description = "모집글 제목 (변경 시에만 전달)", example = "반포 한강 러닝 모집")
            String title,

            @Schema(description = "모집글 내용 (변경 시에만 전달)", example = "페이스 무관 환영합니다!")
            String content,

            @Schema(description = "최대 참여 인원 (변경 시에만 전달, 2~5)", example = "3")
            Integer maxParticipants,

            @Schema(description = "러닝 시작 시간 (변경 시에만 전달)", example = "2026-06-10T20:00:00")
            LocalDateTime startTime,

            @Schema(description = "러닝 종료 예정 시간 (변경 시에만 전달)", example = "2026-06-10T21:00:00")
            LocalDateTime endTime,

            @Schema(description = "러닝 목표 거리 (변경 시에만 전달, km)", example = "7")
            Integer distance,

            @Schema(description = "러닝 장소 주소 (변경 시에만 전달)", example = "서울특별시 서초구 반포동 1")
            String address,

            @Schema(description = "러닝 장소명 (변경 시에만 전달)", example = "반포 한강공원 2주차")
            String location
    ) {}
}
