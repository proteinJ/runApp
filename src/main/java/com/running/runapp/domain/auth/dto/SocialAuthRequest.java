package com.running.runapp.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class SocialAuthRequest {
    public record TokenLogin(
            @NotBlank String accessToken
    ) {}
}