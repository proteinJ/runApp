package com.running.runapp.domain.myroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class MyRoomResponse {

    @Builder
    @Schema(name = "MyRoomResult", description = "마이룸 조회 응답")
    public record Result(
            @Schema(description = "회원 ID", example = "1")
            Long memberId,

            @Schema(description = "닉네임", example = "세찬")
            String nickname,

            @Schema(description = "장착 칭호", example = "Dawn Runner")
            String equippedTitle,

            @Schema(description = "현재 코어 컬러 코드", example = "CORE_ORANGE")
            String currentColorCode,

            @Schema(description = "코어 컬러 목록(팔레트)")
            List<ColorItem> colors
    ) {}

    @Builder
    @Schema(name = "MyRoomColorItem", description = "코어 컬러 아이템")
    public record ColorItem(
            @Schema(description = "컬러 코드", example = "CORE_ORANGE")
            String code,

            @Schema(description = "표시 이름", example = "주황색 코어")
            String name,

            @Schema(description = "HEX", example = "#F57C00")
            String hex,

            @Schema(description = "보유 여부(구매했는지)", example = "true")
            boolean owned
    ) {}

    @Builder
    @Schema(name = "ChangeCoreColorResult", description = "코어 컬러 변경 응답")
    public record ChangeCoreColorResult(
            @Schema(description = "변경된 코어 컬러 코드", example = "CORE_ORANGE")
            String currentColorCode
    ) {}
}
