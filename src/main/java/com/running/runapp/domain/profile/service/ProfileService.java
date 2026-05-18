package com.running.runapp.domain.profile.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.dto.ProfileResponse;
import com.running.runapp.domain.profile.repository.ProfileRepository;
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

    public ProfileResponse.MyProfile getMyProfile(Member member) {
        Profile profile = profileRepository.findByMember(member)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        return ProfileResponse.MyProfile.builder()
                .level(0)
                .totalDistance(profile.getTotalDistance())
                .nickname(profile.getNickname())
                .avgPace(profile.getAvgPace())
                .equipedTitle(profile.getEquippedTitle())
                .build();
    }
}
