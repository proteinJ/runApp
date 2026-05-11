package com.running.runapp.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class SocialRequest {

    // 닉네임으로 친구 신청
    public record FollowSend(
            @NotBlank String targetNickname
    ) {}

    // 친구 피드 조회
    @Builder
    public record Feed(
            @NotNull Integer size   // ex) 30
    ) {}
}