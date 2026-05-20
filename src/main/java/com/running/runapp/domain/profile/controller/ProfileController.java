package com.running.runapp.domain.profile.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.domain.Follow;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.dto.ProfileResponse;
import com.running.runapp.domain.profile.dto.SocialResponse;
import com.running.runapp.domain.profile.repository.FollowRepository;
import com.running.runapp.domain.profile.repository.ProfileRepository;
import com.running.runapp.domain.profile.service.ProfileService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Profile", description = "프로필 관련 API")
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    @Operation(summary = "내 프로필 조회", description = "레벨, 총 달린 거리, 평균 페이스 등 내 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse.MyProfile>> getMyProfile(@LoginMember Member member) {

        ProfileResponse.MyProfile myProfileRes = profileService.getMyProfile(member);

        return ResponseEntity.ok(ApiResponse.success("내 프로필 조회 성공", myProfileRes));
    }

    @Operation(summary = "칭호 장착", description = "소유하고 있는 칭호 장착")
    @PostMapping("/equip")
    public ResponseEntity<ApiResponse<ProfileResponse.MyProfileTitle>> equipTitle(
            @LoginMember Member me,
            @RequestParam("titleId") Long titleId
    ) {
        ProfileResponse.MyProfileTitle myProfileTitleRes = profileService.equipProfileTitle(me, titleId);
        return ResponseEntity.ok(ApiResponse.success("칭호 장착 성공", myProfileTitleRes));
    }

    @Operation(summary = "유저 검색", description = "닉네임으로 유저 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SocialResponse.SearchMemberSummary>>> searchByNickname(
            @LoginMember Member member,
            @RequestParam("nickname") String nickname
    ) {
        List<Profile> profiles = profileRepository.findByNicknameContainingIgnoreCase(nickname);

        List<SocialResponse.SearchMemberSummary> result = profiles.stream()
                .filter(m -> !m.getId().equals(member.getId()))
                .map(m -> new SocialResponse.SearchMemberSummary(
                        m.getId(),
                        m.getNickname(),
                        relationStatus(member.getId(), m.getId())
                ))
                .toList();

        return ResponseEntity.ok(ApiResponse.success("유저 검색 완료", result));
    }

    private String relationStatus(Long me, Long other) {
        List<Follow> relations = followRepository.findRelationBetween(me, other);
        if (relations.isEmpty()) return "NONE";

        Follow f = relations.get(0);

        if (f.getStatus() == Follow.FollowStatus.ACCEPTED) return "FRIEND";

        if (f.getFollower().getId().equals(me)) return "PENDING_SENT";
        return "PENDING_RECEIVED";
    }
}
