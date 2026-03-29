package com.running.runapp.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.error.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 응답 헤더 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 설정

        // 우리가 만든 ApiResponse 형식으로 에러 메시지 구성
        ApiResponse<String> errorResponse = ApiResponse.error(
                ErrorCode.MEMBER_NOT_FOUND.getMessage(), // 혹은 별도의 UNAUTHORIZED 에러코드 사용
                "인증이 필요한 서비스입니다."
        );

        // JSON으로 변환하여 응답 바디에 쓰기
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
