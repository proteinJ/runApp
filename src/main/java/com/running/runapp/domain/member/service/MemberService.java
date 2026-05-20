package com.running.runapp.domain.member.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.domain.Role;
import com.running.runapp.domain.member.domain.TokenDto;
import com.running.runapp.domain.member.dto.MemberRequest;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.member.repository.RefreshTokenRepository;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.domain.ProfileTitle;
import com.running.runapp.domain.profile.domain.Title;
import com.running.runapp.domain.profile.repository.ProfileRepository;
import com.running.runapp.domain.profile.repository.ProfileTitleRepository;
import com.running.runapp.domain.profile.repository.TitleRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import com.running.runapp.global.security.JwtProvider;
import com.running.runapp.global.security.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TitleRepository titleRepository;
    private final ProfileTitleRepository profileTitleRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(MemberRequest.Join req) {
        validateDuplicateMember(req);

        String encodedPassword = passwordEncoder.encode(req.password());

        Member member = Member.builder()
                .email(req.email())
                .password(encodedPassword)
                .realname(req.realname())
                .role(Role.USER)
                .build();

        Profile profile = Profile.builder()
                .nickname(req.nickname())
                .totalPoint(0)
                .totalDistance(0.0)
                .avgPace(0.0)
                .build();

        member.setProfile(profile);

        Member savedMember = memberRepository.save(member);

        Title defaultTitle = titleRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ErrorCode.TITLE_NOT_FOUND));

        ProfileTitle grantedTitle = ProfileTitle.grantTitle(profile, defaultTitle);
        profileTitleRepository.save(grantedTitle);

        profile.equipTitle(grantedTitle);

        return savedMember.getId();
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenDto login(MemberRequest.Login req) {
        try {
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(req.email(), req.password());

            Authentication authentication = authenticationManager.authenticate(token);

            TokenDto tokenDto = jwtProvider.createToken(authentication);

            RefreshToken refreshToken = RefreshToken.builder()
                    .key(authentication.getName())          // 보통 email
                    .value(tokenDto.getRefreshToken())
                    .build();

            refreshTokenRepository.save(refreshToken);

            return tokenDto;

        } catch (BadCredentialsException e) {
            log.warn("로그인 실패(비밀번호 불일치): {}", req.email());
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);

        } catch (InternalAuthenticationServiceException e) {
            log.warn("로그인 실패(계정 없음): {}", req.email());
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);

        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }
    }

    @Transactional
    public void logout(String bearerToken) {
        String accessToken = extractToken(bearerToken);

        if (!jwtProvider.validateToken(accessToken)) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }

        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        Long expirationMs = jwtProvider.getExpiration(accessToken);

        // Redis에 블랙리스트 저장
        redisTemplate.opsForValue().set(accessToken, "logout", expirationMs, TimeUnit.MILLISECONDS);

        // RefreshToken 삭제
        refreshTokenRepository.deleteByKey(authentication.getName());
    }

    @Transactional
    public void changePassword(String email, String bearerToken, MemberRequest.PasswordChange dto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 기존 비밀번호 검증
        if (!passwordEncoder.matches(dto.oldPassword(), member.getPassword())) {
            log.warn("비밀번호 변경 실패(기존 비번 불일치): {}", email);
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        // 변경
        String encodedNew = passwordEncoder.encode(dto.newPassword());
        member.updatePassword(encodedNew); // <- Member 엔티티에 메서드 없으면 추가해야 함

        // 변경했으니 토큰 무효화(로그아웃)
        logout(bearerToken);
    }

    /**
     * 중복 검증
     */
    private void validateDuplicateMember(MemberRequest.Join req) {
        if (memberRepository.existsByEmail(req.email())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATION);
        }
        if (profileRepository.findByNickname(req.nickname()).isPresent()) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATION);
        }
    }

    private String extractToken(String bearerToken) {
        if (bearerToken == null) return "";
        return bearerToken.toLowerCase().startsWith("bearer ")
                ? bearerToken.substring(7)
                : bearerToken;
    }
}