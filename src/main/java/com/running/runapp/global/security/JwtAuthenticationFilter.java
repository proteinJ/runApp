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
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "/actuator/prometheus".equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("JWT filter entered: uri={}, method={}", request.getRequestURI(), request.getMethod());

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        log.debug("Authorization header present={}", StringUtils.hasText(authHeader));

        String jwt = resolveToken(request);
        log.debug("JWT token resolved={}", StringUtils.hasText(jwt));

        if (StringUtils.hasText(jwt)) {
            try {
                jwtProvider.validateTokenOrThrow(jwt);

                String logout = (String) redisTemplate.opsForValue().get(jwt);
                log.debug("Redis logout token exists={}", StringUtils.hasText(logout));

                if (ObjectUtils.isEmpty(logout)) {
                    Authentication authentication = jwtProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authentication set: principal={}", authentication.getName());
                } else {
                    log.warn("JWT rejected: logged-out token, code=A003");
                    request.setAttribute(JWT_ERROR_CODE_ATTR, "A003");
                }
            } catch (ExpiredJwtException e) {
                log.warn("JWT rejected: expired token, code=A002");
                request.setAttribute(JWT_ERROR_CODE_ATTR, "A002");
            } catch (JwtException e) {
                log.warn("JWT rejected: invalid token, code=A003");
                request.setAttribute(JWT_ERROR_CODE_ATTR, "A003");
            }
        } else {
            log.debug("JWT token not provided");
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
