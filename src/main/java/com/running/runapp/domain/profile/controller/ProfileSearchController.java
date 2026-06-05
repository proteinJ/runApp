package com.running.runapp.domain.profile.controller;

import com.running.runapp.domain.profile.domain.Follow;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.dto.SocialResponse;
import com.running.runapp.domain.profile.repository.FollowRepository;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.repository.ProfileRepository;
import com.running.runapp.domain.running.dto.UnitUtils;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class ProfileSearchController {

    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;

    /**
     * 닉네임으로 유저 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SocialResponse.SearchMemberSummary>>> searchByNickname(
            @LoginMember Member me,
            @RequestParam("nickname") String nickname
    ) {
        List<Profile> profiles = profileRepository.findByNicknameContainingIgnoreCase(nickname);

        List<SocialResponse.SearchMemberSummary> result = profiles.stream()
                .filter(m -> !m.getMember().getId().equals(me.getId()))
                .map(m -> new SocialResponse.SearchMemberSummary(
                        m.getMember().getId(),
                        m.getNickname(),
                        m.getLevel(),
                        UnitUtils.metersToKm(m.getTotalDistance()),
                        m.getAvgPace(),
                        m.getEquippedTitle() != null ? m.getEquippedTitle().getName() : null,
                        relationStatus(me.getId(), m.getMember().getId())
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