package com.running.runapp.domain.auth.controller;

import com.running.runapp.domain.auth.dto.SocialAuthRequest;
import com.running.runapp.domain.auth.service.SocialAuthService;
import com.running.runapp.domain.member.domain.TokenDto;
import com.running.runapp.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class SocialAuthController {

    private final SocialAuthService socialAuthService;

    @PostMapping("/kakao/token")
    public ResponseEntity<ApiResponse<TokenDto>> kakaoTokenLogin(
            @RequestBody @Valid SocialAuthRequest.TokenLogin req
    ) {
        TokenDto token = socialAuthService.loginWithKakaoAccessToken(req.accessToken());
        return ResponseEntity.ok(ApiResponse.success("카카오 로그인 성공", token));
    }
}