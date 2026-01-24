package com.running.runapp.domain.member.controller;

import com.running.runapp.domain.member.domain.TokenDto;
import com.running.runapp.domain.member.dto.JoinRequest;
import com.running.runapp.domain.member.dto.LoginRequest;
import com.running.runapp.domain.member.service.MemberService;
import com.running.runapp.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
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
}
