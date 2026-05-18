package com.running.runapp.domain.member.dto;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.domain.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    // 토큰 정보
    @Schema(description = "토큰 종류")
    private String grantType;

    @Schema(description = "AT 값")
    private String accessToken;

    @Schema(description = "RT 값")
    private String refreshToken;

    @Schema(description = "AT 만료시간")
    private Long accessTokenExpiresIn;

    // 사용자 정보
    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "역할")
    private String role;


    public static LoginResponse of(TokenDto tokenDto, Member member) {
        return LoginResponse.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                .role(member.getRole().name())
                .build();
    }
}
