package com.running.runapp.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("### 필터 진입 - URI: {}, Method: {}", request.getRequestURI(), request.getMethod());

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        log.info("### Authorization Header 원본: {}", authHeader);

        String jwt = resolveToken(request);
        log.info("### 추출된 토큰: {}", jwt);

        if (StringUtils.hasText(jwt)) {
            boolean valid = jwtProvider.validateToken(jwt);
            log.info("### validateToken 결과: {}", valid);

            if (valid) {
                String logout = (String) redisTemplate.opsForValue().get(jwt);
                log.info("### Redis logout 조회 결과: {}", logout);

                if (ObjectUtils.isEmpty(logout)) {
                    Authentication authentication = jwtProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("### 인증객체 세팅 완료: principal={}", authentication.getPrincipal());
                } else {
                    log.warn("### 로그아웃 처리된 토큰으로 판단 -> 인증 세팅 안 함");
                }
            } else {
                log.warn("### 토큰 검증 실패 -> 인증 세팅 안 함");
            }
        } else {
            log.warn("### 토큰 없음 -> 인증 세팅 안 함");
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}