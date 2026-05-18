package com.running.runapp.domain.profile.dto;

import com.running.runapp.domain.profile.domain.Title;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class ProfileResponse {

    @Schema(description = "내 정보 조회 반환값")
    @Builder
    public record MyProfile(
            @Schema(description = "닉네임", example = "얌얌욤욤")
            @NotBlank
            String nickname,

            @Schema(description = "레벨", example = "12")
            @NotNull
            Integer level,

            @Schema(description = "총 거리", example = "124.8")
            @NotNull
            Double totalDistance,

            @Schema(description = "평균 페이스", example = "5.42")
            @NotNull
            Double avgPace,

            @Schema(description = "장착한 칭호", example = "런린이")
            @NotNull
            Title equipedTitle
    ) {}
}
