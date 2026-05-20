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


}
