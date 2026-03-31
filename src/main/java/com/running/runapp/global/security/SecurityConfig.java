package com.running.runapp.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security 총괄 설정
 * FilterChain, 암호화 Bean ... etc
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // JWT 사용 하므로 세션 생성하지 않도록 설정 (무상태성 STATELESS)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 예외 핸들링 설정
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(authenticationEntryPoint)
            )


            // API별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/v1/member/join", "/api/v1/member/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/api/v1/member/password", "/api/v1/groups/**").authenticated()
                    .anyRequest().authenticated()
            )

            // 일반적인 로그인 필터(UsernamePasswordAuthenticationFilter) 작동 전 내가 만든 필터 체인 먼저 실행되도록
            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, redisTemplate),
                    UsernamePasswordAuthenticationFilter.class);
            System.out.println("jwt 로그인 필터 성공");
        
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}