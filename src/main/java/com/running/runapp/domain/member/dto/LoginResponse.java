package com.running.runapp.domain.member.dto;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.domain.TokenDto;
import com.running.runapp.domain.profile.domain.Profile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    // 토큰 정보
    @Schema(description = "토큰 종류", example = "Bearer")
    private String grantType;

    @Schema(description = "AT 값", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZUBuYXZlci5jb20iLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzcwOTA4MjQxfQ.7mVWxIEHxv2n8ZFiB04A1cSZk1naaZj8xVFC23vlo14")
    private String accessToken;

    @Schema(description = "RT 값", example = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NzE1MTEyNDF9.aFrQuRDDFzd-OuXtgTPkLoe0KBRwtsSf-crCjgTUKNk")
    private String refreshToken;

    @Schema(description = "AT 만료시간", example = "1770908241768")
    private Long accessTokenExpiresIn;

    // 사용자 정보
    @Schema(description = "닉네임", example = "프로틴괴물")
    private String nickname;

    @Schema(description = "역할", example = "ADMIN")
    private String role;

    @Schema(description = "장착 색깔 코드", example = "CORE_RAINBOW")
    private String coreColorCode;

    @Schema(description = "장착 칭호", example = "TITLE_001_GOLD")
    private String equippedTitleCode;


    public static LoginResponse of(TokenDto tokenDto, Member member, Profile profile) {
        return LoginResponse.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                .nickname(profile.getNickname())
                .role(member.getRole().name())
                .coreColorCode(normalizeCoreColorCode(profile.getCoreColorCode()))
                .equippedTitleCode(profile.getEquippedTitle().getTitleCode())
                .build();
    }

    private static String normalizeCoreColorCode(String colorCode) {
        if (colorCode == null || colorCode.isBlank()) {
            return "CORE_ORANGE";
        }
        if (colorCode.startsWith("CHAR_")) {
            return "CORE_" + colorCode.substring("CHAR_".length());
        }
        return colorCode;
    }
}
