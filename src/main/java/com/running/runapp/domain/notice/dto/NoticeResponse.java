package com.running.runapp.domain.notice.dto;

import com.running.runapp.domain.notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class NoticeResponse {

    @Schema(description = "공지사항 목록 아이템")
    public record Summary(
        @Schema(description = "공지사항 ID") Long noticeId,
        @Schema(description = "제목") String title,
        @Schema(description = "작성 시각") LocalDateTime createdAt,
        @Schema(description = "상단 고정 여부") boolean isPinned
    ) {
        public static Summary from(Notice notice) {
            return new Summary(notice.getId(), notice.getTitle(), notice.getCreatedAt(), notice.isPinned());
        }
    }

    @Schema(description = "공지사항 상세")
    public record Detail(
        @Schema(description = "공지사항 ID") Long noticeId,
        @Schema(description = "제목") String title,
        @Schema(description = "내용") String content,
        @Schema(description = "작성 시각") LocalDateTime createdAt,
        @Schema(description = "상단 고정 여부") boolean isPinned
    ) {
        public static Detail from(Notice notice) {
            return new Detail(notice.getId(), notice.getTitle(), notice.getContent(), notice.getCreatedAt(), notice.isPinned());
        }
    }
}
