package com.running.runapp.domain.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class ProfileResponse {

    @Schema(description = "내 정보 조회 반환값")
    @Builder
    public record MyProfile(
            @Schema(description = "닉네임", example = "얌얌욤욤")
            String nickname,

            @Schema(description = "레벨", example = "12")
            Integer level,

            @Schema(description = "총 거리", example = "124.8")
            Double totalDistance,

            @Schema(description = "평균 페이스", example = "5.42")
            Double avgPace
    ) {}
}
