package com.running.runapp.domain.myroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class MyRoomRequest {

    // GET은 @ModelAttribute로 받으라고 해서 "빈 Query"라도 만들어둠
    @Schema(name = "MyRoomQuery", description = "마이룸 조회 쿼리(현재는 파라미터 없음)")
    public record Query() {}

    @Schema(name = "ChangeCoreColorRequest", description = "코어 컬러 변경 요청")
    public record ChangeCoreColor(
            @NotBlank
            @Schema(description = "변경할 코어 컬러 코드", example = "CORE_RED")
            String colorCode
    ) {}
}