package com.running.runapp.domain.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth.kakao")
public class SocialAuthProperties {
    private String clientId;
    private String clientSecret; // 없으면 빈 문자열로 두면 됨
}