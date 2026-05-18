package com.running.runapp.domain.profile.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.domain.Title;
import com.running.runapp.domain.profile.dto.TitleRequest;
import com.running.runapp.domain.profile.dto.TitleResponse;
import com.running.runapp.domain.profile.repository.TitleRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
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

    public List<TitleResponse.TitleInfo> getAllTitle(Member member) {

        List<Title> titleList = titleRepository.findAll();

        return titleList.stream()
                .map(TitleResponse.TitleInfo::from)
                .toList();
    }

    @Transactional
    public TitleResponse.TitleInfo addNewTitle(TitleRequest.addNewTitle dto) {
        if (titleRepository.existsByTitleCode(dto.titleCode())) {
            log.warn("칭호 등록 실패 - 이미 존재하는 칭호 코드: {}", dto.titleCode());
            throw new BusinessException(ErrorCode.DUPLICATE_TITLE_CODE);
        }

        Title savedTitle = titleRepository.save(dto.toEntity());

        log.info("새로운 시스템 칭호 등록 완료 - ID: {}, Code: {}, Name: {}",
                savedTitle.getId(), savedTitle.getTitleCode(), savedTitle.getName());

        // 엔티티를 응답 DTO로 변환하여 최종 리턴
        return TitleResponse.TitleInfo.from(savedTitle);
    }
}
