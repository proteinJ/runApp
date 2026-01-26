package com.running.runapp.domain.member.controller;

import com.running.runapp.domain.member.domain.TokenDto;
import com.running.runapp.domain.member.dto.JoinRequest;
import com.running.runapp.domain.member.dto.LoginRequest;
import com.running.runapp.domain.member.dto.PasswordChangeRequest;
import com.running.runapp.domain.member.service.MemberService;
import com.running.runapp.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinRequest dto) {
        Long memberId = memberService.join(dto);

        return ResponseEntity.ok(ApiResponse.success("회원가입 완료", memberId));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest dto) {
        TokenDto tokenDto = memberService.login(dto);
        return ResponseEntity.ok(ApiResponse.success("로그인 완료", tokenDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        memberService.logout(bearerToken);

        return ResponseEntity.ok(ApiResponse.success("로그아웃 완료"));
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody @Valid PasswordChangeRequest dto
        )
         {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        log.info("### 인증 객체: " + auth);
        if (auth != null) {
            log.info("### Principal 타입: " + auth.getPrincipal().getClass());
        }

        memberService.changePassword(userDetails.getUsername(), bearerToken, dto);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경 완료 - 다시 로그인해주세요."));
    }
}
