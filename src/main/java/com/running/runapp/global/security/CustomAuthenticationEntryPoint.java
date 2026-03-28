package com.running.runapp.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.running.runapp.global.error.ErrorCode;
import com.running.runapp.global.error.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper; // JSON 변환을 위한 스프링 빈

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // 1. 응답 헤더 설정 (JSON 타입)
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 2. ErrorCode를 활용해 응답 데이터 생성
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.AUTHENTICATION_FAILED);

        // 3. ObjectMapper를 이용해 객체를 JSON 문자열로 변환하여 전송
        String result = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(result);
    }
}
