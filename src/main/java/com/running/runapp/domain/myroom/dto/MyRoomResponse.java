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

            @Schema(description = "현재 장착 칭호명", example = "새싹 러너")
            String equippedTitle,

            @Schema(description = "현재 코어 컬러 코드", example = "CORE_ORANGE")
            String currentColorCode,

            @Schema(description = "코어 컬러 목록(팔레트)")
            List<ColorItem> colors,

            @Schema(description = "전체 칭호 목록 (보유 여부 포함)")
            List<TitleItem> titles
    ) {}

    @Builder
    @Schema(name = "MyRoomTitleItem", description = "칭호 아이템")
    public record TitleItem(
            @Schema(description = "칭호 ID", example = "1")
            Long titleId,

            @Schema(description = "칭호 코드", example = "TITLE_001_START")
            String titleCode,

            @Schema(description = "칭호 이름", example = "새싹 러너")
            String name,

            @Schema(description = "등급", example = "NORMAL",
                    allowableValues = {"NORMAL", "RARE", "EPIC", "LEGENDARY"})
            String rarity,

            @Schema(description = "획득 조건 설명", example = "러닝을 처음 시작한 당신에게!")
            String description,

            @Schema(description = "보유 여부(획득했는지)", example = "true")
            boolean owned
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
