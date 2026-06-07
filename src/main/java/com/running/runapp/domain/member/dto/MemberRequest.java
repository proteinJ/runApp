package com.running.runapp.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemberRequest {

    @Schema(description = "회원가입 요청")
    public record Join (
        @Schema(description = "사용자 이메일", example = "username1234@naver.com")
        @Email(message = "이메일 형식이 아닙니다.") @NotBlank
        String email,

        @Schema(description = "비밀번호", example = "password1234")
        @NotBlank
        @Size(min = 8, max =30, message = "비밀번호는 8자리 이상 30자리 이하이어야 합니다.")
        String password,

        @Schema(description = "닉네임", example = "얌얌욤욤")
        @NotBlank
        @Size(min = 2, message = "닉네임은 2글자 이상이어야 합니다.")
        String nickname,

        @Schema(description = "실명", example = "박재현")
        @NotBlank
        String realname
    ) {}

    @Schema(description = "로그인 요청")
    public record Login (
        @Schema(description = "이메일", example = "username1234@naver.com")
        @Email(message = "이메일 형식이 아닙니다.")
        @NotBlank
        String email,

        @Schema(description = "비밀번호", example = "password1234")
        @NotBlank
        @Size(min = 8, max = 30, message = "비밀번호는 8자리 이상 30자리 이하이어야 합니다.")
        String password
    ) {}

    @Schema(description = "비밀번호 변경 요청")
    public record PasswordChange (
        @Schema(name = "이전 비밀번호", example = "username1234@naver.com")
        @NotBlank String oldPassword,

        @Schema(name = "새로운 비밀번호", example = "mskim5322@naver.com")
        @NotBlank @Size(min = 8) String newPassword
    ) {}

    @Schema(description = "회원 탈퇴 요청")
    public record Withdraw(
        @Schema(description = "현재 비밀번호 (본인 확인용)", example = "password1234")
        @NotBlank String password
    ) {}
}
