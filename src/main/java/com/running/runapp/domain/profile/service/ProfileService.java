package com.running.runapp.domain.profile.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.domain.ProfileTitle;
import com.running.runapp.domain.profile.dto.ProfileResponse;
import com.running.runapp.domain.profile.repository.ProfileRepository;
import com.running.runapp.domain.profile.repository.ProfileTitleRepository;
import com.running.runapp.domain.running.dto.UnitUtils;
import com.running.runapp.global.common.LevelCalculator;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileTitleRepository profileTitleRepository;

    public ProfileResponse.MyProfile getMyProfile(Member member) {
        Profile profile = profileRepository.findByMember(member)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        return ProfileResponse.MyProfile.builder()
                .level(profile.getLevel())
                .totalDistance(UnitUtils.metersToKm(profile.getTotalDistance()))
                .nickname(profile.getNickname())
                .avgPace(profile.getAvgPace())
                .equippedTitleName(profile.getEquippedTitle().getName())
                .build();
    }

    @Transactional
    public ProfileResponse.MyProfileTitle equipProfileTitle(Long memberId, Long targetTitleId) {

        ProfileTitle myProfileTitle = profileTitleRepository.findWithProfileByMemberIdAndTitleId(memberId, targetTitleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_YOUR_TITLE));

        Profile profile = myProfileTitle.getProfile();

        profile.equipTitle(myProfileTitle);

        return ProfileResponse.MyProfileTitle.builder()
                .titleId(myProfileTitle.getTitle().getId())
                .name(myProfileTitle.getTitle().getName())
                .build();
    }

    @Transactional
    public ProfileResponse.ExpRewardResult rewardExp(Long memberId, Long gainedExp) {
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        long totalExp = profile.addExp(gainedExp);

        int finalLevel = LevelCalculator.calculateLevelFromExp(profile.getTotalExp());

        boolean isLevelUp = profile.updateLevel(finalLevel);

        return new ProfileResponse.ExpRewardResult(isLevelUp, finalLevel, totalExp);
    }
}
