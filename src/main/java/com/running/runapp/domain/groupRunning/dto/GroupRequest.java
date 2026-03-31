package com.running.runapp.domain.groupRunning.dto;

import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import com.running.runapp.domain.groupRunning.domain.GroupStatus;
import com.running.runapp.domain.member.domain.Member;

import java.time.LocalDateTime;

public class GroupRequest {

    public record groupAdd(
        String title,
        String content,
        Integer maxParticipants,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer distance,
        String location,
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

    public record UpdateExtraRequest(
       String title,
       String content,
       Integer maxParticipants,
       LocalDateTime startTime,
       LocalDateTime endTime,
       Integer distance,
       String address,
       String location
    ) {}



}
