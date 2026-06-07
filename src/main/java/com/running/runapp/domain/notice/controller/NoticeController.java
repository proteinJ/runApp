package com.running.runapp.domain.notice.controller;

import com.running.runapp.domain.notice.dto.NoticeResponse;
import com.running.runapp.domain.notice.service.NoticeService;
import com.running.runapp.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notice", description = "공지사항 API")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 목록", description = "고정 공지 우선, 이후 최신순 정렬")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeResponse.Summary>>> getNotices() {
        return ResponseEntity.ok(ApiResponse.success("공지사항 목록 조회 성공", noticeService.getNotices()));
    }

    @Operation(summary = "공지사항 상세")
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponse.Detail>> getNotice(@PathVariable Long noticeId) {
        return ResponseEntity.ok(ApiResponse.success("공지사항 조회 성공", noticeService.getNotice(noticeId)));
    }
}
