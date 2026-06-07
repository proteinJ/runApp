package com.running.runapp.domain.notice.service;

import com.running.runapp.domain.notice.dto.NoticeResponse;
import com.running.runapp.domain.notice.repository.NoticeRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeResponse.Summary> getNotices() {
        return noticeRepository.findAllByOrderByIsPinnedDescCreatedAtDesc()
                .stream()
                .map(NoticeResponse.Summary::from)
                .toList();
    }

    public NoticeResponse.Detail getNotice(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .map(NoticeResponse.Detail::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));
    }
}
