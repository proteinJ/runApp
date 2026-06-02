package com.running.runapp.domain.profile.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.domain.ProfileTitle;
import com.running.runapp.domain.profile.domain.Title;
import com.running.runapp.domain.profile.dto.TitleResponse;
import com.running.runapp.domain.profile.repository.ProfileTitleRepository;
import com.running.runapp.domain.profile.repository.TitleRepository;
import com.running.runapp.domain.running.domain.RunStatus;
import com.running.runapp.domain.running.repository.RunningRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TitleService {

    private final TitleRepository titleRepository;
    private final ProfileTitleRepository profileTitleRepository;
    private final RunningRecordRepository runningRecordRepository;

    public List<TitleResponse.TitleInfo> getAllTitle(Member member) {

        List<Title> titleList = titleRepository.findAll();

        return titleList.stream()
                .map(TitleResponse.TitleInfo::from)
                .toList();
    }


    @Transactional
    public void checkAndGrantTitles(Profile profile) {

        // 조건 확인
        if (runningRecordRepository.existsByMemberAndStatus(profile.getMember(), RunStatus.FINISHED)) {
            grantIfNotOwned(profile, "TITLE_002"); // 런린이
        }
        if (runningRecordRepository.existsSpeedKingRecord(profile.getMember().getId())) {
            grantIfNotOwned(profile, "TITLE_003"); // 스피드 킹(5km 평균 페이스 4분대 기록)
        }
        if (profile.getTotalDistance() > 42.195) {
            grantIfNotOwned(profile, "TITLE_004"); // 마라토너
        }

    }

    /**
     * 편의 메서드
     */

    // 해당 칭호를 소유 하고 있는지 검증 메서드
    private void grantIfNotOwned(Profile profile, String titleCode) {
        // 이미 가지고 있음
        if (profileTitleRepository.existsByProfileAndTitleTitleCode(profile, titleCode))
            return;

        // 유저에게 지급
        titleRepository.findByTitleCode(titleCode).ifPresent(title ->
                profileTitleRepository.save(ProfileTitle.grantTitle(profile, title))
        );
    }
}
