package com.running.runapp.domain.groupRunning.dto;

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
    ) {}

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
