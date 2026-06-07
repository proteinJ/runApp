package com.running.runapp.domain.member.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.dto.LoginResponse;
import com.running.runapp.domain.member.dto.MemberRequest;
import com.running.runapp.domain.member.service.MemberService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Member", description = "계정 관련 API")
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "새로운 사용자 회원가입")
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Long>> join(@RequestBody MemberRequest.Join dto) {
        Long memberId = memberService.join(dto);

        return ResponseEntity.ok(ApiResponse.success("회원가입 완료", memberId));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody MemberRequest.Login dto) {
        LoginResponse res = memberService.login(dto);
        return ResponseEntity.ok(ApiResponse.success("로그인 완료", res));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken) {
        memberService.logout(bearerToken);

        return ResponseEntity.ok(ApiResponse.success("로그아웃 완료"));
    }

    @Operation(summary = "회원 탈퇴", description = "비밀번호 확인 후 계정 삭제")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @LoginMember Member member,
            @Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken,
            @RequestBody @Valid MemberRequest.Withdraw dto
    ) {
        memberService.withdraw(member, bearerToken, dto);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }

    @Operation(summary = "비밀번호 변경", description = "기존 비번 확인 후 새 비번으로 변경 (변경 후 토큰 만료)")
    @PostMapping("/password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken,
            @RequestBody @Valid MemberRequest.PasswordChange dto
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
