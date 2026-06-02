package com.running.runapp.domain.point.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.point.dto.PointResponse;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.repository.ProfileRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final ProfileRepository profileRepository;

    public PointResponse.CurrentPointResult getCurrentPoints(Member me) {
        Profile profile = profileRepository.findByMemberId(me.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        Integer currentTotalPoints = profile.getTotalPoint() == null ? 0 : profile.getTotalPoint();

        return PointResponse.CurrentPointResult.builder()
                .currentTotalPoints(currentTotalPoints)
                .build();
    }
}
