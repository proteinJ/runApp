package com.running.runapp.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class AdminNoticeRequest {

    @Schema(description = "공지사항 생성 요청")
    public record create(
        @Schema(description = "제목") @NotBlank String title,
        @Schema(description = "내용") @NotBlank String content,
        @Schema(description = "상단 고정 여부", defaultValue = "false") Boolean isPinned
    ) {}

    @Schema(description = "공지사항 수정 요청 (null 필드는 변경하지 않음)")
    public record update(
        @Schema(description = "제목") String title,
        @Schema(description = "내용") String content,
        @Schema(description = "상단 고정 여부") Boolean isPinned
    ) {}
}
