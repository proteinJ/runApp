package com.running.runapp.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 30, message = "비밀번호는 8자리 이상 30자리 이하이어야 합니다.")
    private String password;
}
