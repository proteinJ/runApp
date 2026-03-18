package com.running.runapp.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemberRequest {

    public record Join (
        @Email(message = "이메일 형식이 아닙니다.") @NotBlank
        String email,

        @NotBlank
        @Size(min = 8, max =30, message = "비밀번호는 8자리 이상 30자리 이하이어야 합니다.")
        String password,

        @NotBlank
        @Size(min = 2, message = "닉네임은 2글자 이상이어야 합니다.")
        String nickname,

        @NotBlank
        String realname
    ) {}

    public record Login (
        @Email(message = "이메일 형식이 아닙니다.")
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 8, max = 30, message = "비밀번호는 8자리 이상 30자리 이하이어야 합니다.")
        String password
    ) {}

    public record PasswordChange (
        @NotBlank String oldPassword,
        @NotBlank @Size(min = 8) String newPassword
    ) {}
}
