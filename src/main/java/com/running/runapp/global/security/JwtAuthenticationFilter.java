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

// 모든 HTTP 요청이 컨트롤러에 도달하기 전에 이 필터를 거치며, "토큰이 유효한지", "누가 보낸 요청인지"를 확인
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final RedisTemplate<Object, Object> redisTemplate;

    // 1. 실제 필터링 로직이 수행되는 곳
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        log.info("### 필터 진입 - URI: {}, Method: {}", request.getRequestURI(), request.getMethod());

        // 2. 요청 헤더에서 JWT 토큰 추출 메서드 호출
        String jwt = resolveToken(request);
        log.info("### 추출된 토큰: {}", jwt);

        // 3. 토큰이 존재하고, 유효성 검사 통과 시 인증 정보 가져옴
        if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
            log.info("### 토큰 존재하고, 유효성 검사 통과");
            String Logout = (String) redisTemplate.opsForValue().get(jwt);

            if (ObjectUtils.isEmpty(Logout)) {
                // 4. 토큰으로부터 유저 정보(authentication 객체)를 꺼내옴.
                Authentication authentication = jwtProvider.getAuthentication(jwt);

                // 5. 해당 유저가 인증되었다는 사실을 'SecurityContext'에 저장
                // 저장 이후 Controller에서 @AuthenticationPrincipal로 유저 정보 꺼낼 수 있음.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 6. 다음 필터로 요청을 넘김
        filterChain.doFilter(request, response);
    }

    // 요청 Header에서 JWT 토큰 추출 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
