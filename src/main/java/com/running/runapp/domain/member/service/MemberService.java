package com.running.runapp.domain.member.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.domain.Role;
import com.running.runapp.domain.member.domain.TokenDto;
import com.running.runapp.domain.member.dto.JoinRequest;
import com.running.runapp.domain.member.dto.LoginRequest;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.member.repository.RefreshTokenRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import com.running.runapp.global.security.JwtProvider;
import com.running.runapp.global.security.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(JoinRequest req) {
        // 1. 중복 검증
        validateDuplicateMember(req);

        // 2. 비밀번호 암호화 및 엔티티 생성
        String encodedPassword = passwordEncoder.encode(req.getPassword());

        Member member = Member.builder()
                .email(req.getEmail())
                .password(encodedPassword)
                .nickname(req.getNickname())
                .realname(req.getRealname())
                .role(Role.USER)
                .build();

        return memberRepository.save(member).getId();
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenDto login(LoginRequest req) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());

        // 토큰 인증 확인
        Authentication authentication = authenticationManager.authenticate(token);

        TokenDto tokenDto = jwtProvider.createToken(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }


    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String bearerToken) {
        String accessToken = bearerToken.startsWith("Bearer") ? bearerToken.substring(7) : bearerToken;

        if (!jwtProvider.validateToken(accessToken)) {
            throw new RuntimeException("잘못된 요청입니다.");
        }

        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        Long expiration = jwtProvider.getExpiration(accessToken);

        // Redis에서 AccessToken을 블랙리스트에 등록
        redisTemplate.opsForValue()
                        .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        // RF 삭제
        refreshTokenRepository.deleteByKey(authentication.getName());
    }


    /**
     * 유효성 검사 (중복 회원)
     */
    public void validateDuplicateMember(JoinRequest req) {
        if (memberRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATION);
        } else if (memberRepository.findByNickname(req.getNickname()).isPresent()) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATION);
        }
    }

    /**
     * 회원 조회
     */
    public List<Member> findAll() { return memberRepository.findAll(); }

    public Optional<Member> findOne(Long memberId) { return memberRepository.findById(memberId); }
}
