package com.running.runapp.domain.auth.service;

import com.running.runapp.domain.member.domain.*;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.member.repository.SocialAccountRepository;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.domain.ProfileTitle;
import com.running.runapp.domain.profile.repository.ProfileRepository;
import com.running.runapp.domain.profile.repository.ProfileTitleRepository;
import com.running.runapp.domain.profile.repository.TitleRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import com.running.runapp.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialAuthService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final ProfileRepository profileRepository;
    private final TitleRepository titleRepository;
    private final ProfileTitleRepository profileTitleRepository;
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

        if (member.getProfile() == null) {
            log.warn("프로필 없는 회원 감지 — 자동 생성: memberId={}", member.getId());
            ensureProfile(member, nickname, providerUserId);
        }

        return jwtProvider.createTokenForSocial(member.getId(), member.getEmail(), member.getRole().name());
    }

    private void ensureProfile(Member member, String kakaoNickname, String providerUserId) {
        String profileNickname = (kakaoNickname != null && !kakaoNickname.isBlank())
                ? kakaoNickname
                : "카카오_" + providerUserId;

        Profile profile = Profile.builder()
                .nickname(profileNickname)
                .level(1)
                .totalPoint(0)
                .totalDistance(0.0)
                .avgPace(0.0)
                .coreColorCode("CORE_ORANGE")
                .build();

        // member가 이미 managed 엔티티이므로 Profile 오너 쪽에서 직접 저장
        profile.setMember(member);
        profileRepository.save(profile);

        if (member.getRealname() == null || member.getRealname().isBlank()) {
            member.updateRealname(profileNickname);
        }

        titleRepository.findById(1L).ifPresent(defaultTitle -> {
            ProfileTitle grantedTitle = ProfileTitle.grantTitle(profile, defaultTitle);
            profileTitleRepository.save(grantedTitle);
            profile.equipTitle(grantedTitle);
        });

        log.info("누락 프로필 생성 완료: memberId={}, profileId={}", member.getId(), profile.getId());
    }

    private Member createMemberAndLinkKakao(String providerUserId, String email, String nickname, String profileImageUrl) {
        String generatedEmail = (email != null && !email.isBlank())
                ? email
                : "kakao_" + providerUserId + "@social.local";

        // nickname은 Kakao에서 받은 값을 사용하되, 없으면 providerUserId 기반으로 생성 (unique 보장)
        String profileNickname = (nickname != null && !nickname.isBlank())
                ? nickname
                : "카카오_" + providerUserId;

        log.info("신규 카카오 회원 자동 가입: providerUserId={}, email={}, nickname={}", providerUserId, generatedEmail, profileNickname);

        Member member = Member.builder()
                .email(generatedEmail)
                .password("{noop}SOCIAL")
                .realname(profileNickname)
                .role(Role.USER)
                .build();

        Profile profile = Profile.builder()
                .nickname(profileNickname)
                .level(1)
                .totalPoint(0)
                .totalDistance(0.0)
                .avgPace(0.0)
                .coreColorCode("CORE_ORANGE")
                .build();

        member.setProfile(profile);

        Member saved = memberRepository.save(member);
        log.info("회원/프로필 저장 완료: memberId={}, profileId={}", saved.getId(), profile.getId());

        // 기본 칭호(id=1) 부여
        titleRepository.findById(1L).ifPresent(defaultTitle -> {
            ProfileTitle grantedTitle = ProfileTitle.grantTitle(profile, defaultTitle);
            profileTitleRepository.save(grantedTitle);
            profile.equipTitle(grantedTitle);
        });

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

        ResponseEntity<Map> res;
        try {
            res = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
        } catch (HttpClientErrorException e) {
            log.warn("카카오 API 호출 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }

        Map body = res.getBody();
        if (body == null || body.get("id") == null) {
            log.warn("카카오 응답에 id 없음: body={}", body);
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