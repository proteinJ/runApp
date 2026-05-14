package com.running.runapp.domain.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class SocialRequest {

    @Schema(description = "닉네임으로 친구 신청 요청")
    public record FollowSend(
            @Schema(description = "찾을 닉네임", example = "얌얌욤욤")
            @NotBlank String targetNickname
    ) {}

    @Schema(description = "친구 피드 조회 요청")
    @Builder
    public record Feed(
            @Schema(description = "검색 결과 개수", example = "30")
            @NotNull Integer size
    ) {}
}