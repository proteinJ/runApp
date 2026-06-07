package com.running.runapp.domain.auth.service;

import com.running.runapp.domain.member.domain.*;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.member.repository.SocialAccountRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import com.running.runapp.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SocialAuthService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public TokenDto loginWithKakaoAccessToken(String kakaoAccessToken) {
        KakaoProfile profile = fetchKakaoMe(kakaoAccessToken);

        String providerUserId = profile.id();
        String email = profile.email();
        String nickname = profile.nickname();
        String profileImageUrl = profile.profileImageUrl();

        Member member = socialAccountRepository
                .findByProviderAndProviderUserId(Provider.KAKAO, providerUserId)
                .map(SocialAccount::getMember)
                .orElseGet(() -> createMemberAndLinkKakao(providerUserId, email, nickname, profileImageUrl));

        return jwtProvider.createTokenForSocial(member.getId(), member.getEmail(), member.getRole().name());
    }

    private Member createMemberAndLinkKakao(String providerUserId, String email, String nickname, String profileImageUrl) {
        String generatedEmail = (email != null && !email.isBlank())
                ? email
                : "kakao_" + providerUserId + "@social.local";

        Member member = Member.builder()
                .email(generatedEmail)
                .password("{noop}SOCIAL") // 지금 구조 유지용. 실무면 nullable 또는 별도 필드 추천
                .realname(null)
                .role(Role.USER)
                .build();

        Member saved = memberRepository.save(member);

        SocialAccount sa = SocialAccount.link(
                saved,
                Provider.KAKAO,
                providerUserId,
                email,
                nickname,
                profileImageUrl
        );
        socialAccountRepository.save(sa);

        return saved;
    }

    private KakaoProfile fetchKakaoMe(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> res = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map body = res.getBody();
        if (body == null || body.get("id") == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }

        String id = String.valueOf(body.get("id"));

        Map<String, Object> account = (Map<String, Object>) body.get("kakao_account");
        String email = null;
        String nickname = null;
        String profileImage = null;

        if (account != null) {
            Object emailObj = account.get("email");
            if (emailObj != null) email = emailObj.toString();

            Map<String, Object> profile = (Map<String, Object>) account.get("profile");
            if (profile != null) {
                Object nickObj = profile.get("nickname");
                if (nickObj != null) nickname = nickObj.toString();

                Object imgObj = profile.get("profile_image_url");
                if (imgObj != null) profileImage = imgObj.toString();
            }
        }

        return new KakaoProfile(id, email, nickname, profileImage);
    }

    private record KakaoProfile(String id, String email, String nickname, String profileImageUrl) {}
}