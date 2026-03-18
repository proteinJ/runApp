package com.running.runapp.global.security;

import com.running.runapp.domain.member.domain.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER = "Bearer ";

    // 1. application.yml에서 설정한 값들을 가져옴.
    @Value("${jwt.secret}")
    private String secretkey;

    @Value("${jwt.access-token-validity-in-milliseconds}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-validity-in-milliseconds}")
    private long refreshTokenValidityInMilliseconds;

    private Key key;

    // 2. 빈이 생성된 후, secretKey를 Base64로 인코딩하여 Key 객체로 변환.
    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getEncoder().encode(secretkey.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 3. 유저 정보를 기반으로 AccessToken과 Refresh Token을 생성
    /**
     * AT & RT 생성
     */
    public TokenDto createToken(Authentication auth) {
        String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Long memberId = null;
        if (auth.getPrincipal() instanceof PrincipalDetails principal) {
            memberId = principal.getMemberId();
        }

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + accessTokenValidityInMilliseconds);
        Date refreshTokenExpiresIn = new Date(now + refreshTokenValidityInMilliseconds);

        String accessToken = Jwts.builder()
                .setSubject(auth.getName())
                .claim("memberId", memberId)
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    /**
     * 토큰 정보 추출
     */
    // 4. 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내고, Security가 인식하는 Authentication 객체로 변환
    public Authentication getAuthentication(String accessToken) {

        // 토큰 복호화
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

        Long memberId = claims.get("memberId", Long.class);

        PrincipalDetails principal = new PrincipalDetails(
                memberId,
                claims.getSubject(),
                "",
                authorities
        );

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * 토큰 만료시간 반환 메서드
     */
    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody().getExpiration();

        long now = (new Date()).getTime();

        return expiration.getTime() - now;
    }

    /**
     * 만료된 토큰이라도 정보 추출 메서드 - RF 재발급 시 사용
     */
//    private Claims parseToken(String token) {}


    /**
     * 로그아웃 BlackList 처리를 위해 Token 유효 시간 계산 메서드
     */
//    private Long getExpiration(String token) {}

}
