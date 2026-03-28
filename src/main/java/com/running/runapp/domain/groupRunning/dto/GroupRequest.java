package com.running.runapp.domain.groupRunning.dto;

import java.time.LocalDateTime;

public class GroupRequest {

    public record groupAdd(
        String title,
        String content,
        Integer maxParticipants,
        LocalDateTime startTime
    ) {}

    public record UpdateExtraRequest(
       String title,
       String content,
       Integer maxParticipants,
       LocalDateTime startTime
    ) {}



}
