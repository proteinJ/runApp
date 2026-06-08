package com.running.runapp.global.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
    public static final String JWT_ERROR_CODE_ATTR = "JWT_ERROR_CODE";

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
            try {
                jwtProvider.validateTokenOrThrow(jwt);

                String logout = (String) redisTemplate.opsForValue().get(jwt);
                log.info("### Redis logout 조회 결과: {}", logout);

                if (ObjectUtils.isEmpty(logout)) {
                    Authentication authentication = jwtProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("### 인증객체 세팅 완료: principal={}", authentication.getPrincipal());
                } else {
                    log.warn("### 로그아웃 처리된 토큰 -> A003");
                    request.setAttribute(JWT_ERROR_CODE_ATTR, "A003");
                }
            } catch (ExpiredJwtException e) {
                log.warn("### 만료된 토큰 -> A002");
                request.setAttribute(JWT_ERROR_CODE_ATTR, "A002");
            } catch (JwtException e) {
                log.warn("### 유효하지 않은 토큰 -> A003");
                request.setAttribute(JWT_ERROR_CODE_ATTR, "A003");
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