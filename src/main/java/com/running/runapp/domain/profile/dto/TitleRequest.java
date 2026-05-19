package com.running.runapp.domain.profile.dto;

import com.running.runapp.domain.profile.domain.Rarity;
import com.running.runapp.domain.profile.domain.Title;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

public class TitleRequest {

    @Schema
    @Builder
    public record addNewTitle(
            @Schema(description = "칭호 ID", example = "1")
            @NotNull
            @Positive(message = "칭호 ID는 1 이상인 양수이어야 합니다.")
            Long id,

            @Schema(description = "칭호 이름", example = "42.195")
            @NotBlank
            @Size(min = 1, max = 10, message = "칭호 이름은 1~10 글자 이내이어야 합니다.")
            String name,

            @Schema(description = "프론트와 약속한 코드", example = "TITLE_001_GOLD")
            @NotBlank
            @Size(min = 1, max = 15, message = "칭호 코드는 1~15 글자 이내이어야 합니다.")
            String titleCode,

            @Schema(description = "칭호 등급", example = "EPIC")
            @NotNull
            Rarity rarity,

            @Schema(description = "경험치 보너스 배율", example = "0.05")
            @NotNull
            @DecimalMin(value = "0.01", message = "보너스 비율은 최소 0.01 이상이어야 합니다.")
            Double expBonusRatio,

            @Schema(description = "포인트 보너스 배율", example = "0.06")
            @NotNull
            @DecimalMin(value = "0.01", message = "보너스 비율은 최소 0.01 이상이어야 합니다.")
            Double pointBonusRatio,

            @Schema(description = "칭호 획득 방법 및 설명", example = "일주일 당 42.195 km 씩 1달 동안 달리면 얻을 수 있는 칭호이다.\n" +
                    "꾸준한 사람만이 가질 수 있달까? 이 정도면 나름 러너라고 당당하게 이야기하고 다니자!")
            @NotBlank
            @Size(min = 1, max = 150, message = "칭호 획득 방법 및 설명은 1~150 글자 이내이어야 합니다.")
            String description
    ) {
        public Title toEntity() {
            return Title.builder()
                    .name(this.name)
                    .titleCode(this.titleCode)
                    .rarity(this.rarity)
                    .expBonusRatio(this.expBonusRatio)
                    .pointBonusRatio(this.pointBonusRatio)
                    .description(this.description)
                    .build();
        }
    }

    public record updateTitleInfo(
            @Schema(description = "정보 변경할 칭호 ID", example = "1")
            @NotNull(message = "정보를 변경할 칭호 ID가 누락(Null) 되었습니다.")
            @Positive(message = "칭호 ID는 1 이상인 양수이어야 합니다.")
            Long id,

            @Schema(description = "칭호 이름", example = "42.195")
            @Size(min = 1, max = 10, message = "칭호 이름은 1~10 글자 이내이어야 합니다.")
            String name,

            @Schema(description = "프론트와 약속한 코드", example = "TITLE_001_GOLD")
            @Size(min = 1, max = 15, message = "칭호 코드는 1~15 글자 이내이어야 합니다.")
            String titleCode,

            @Schema(description = "칭호 등급", example = "EPIC")
            Rarity rarity,

            @Schema(description = "경험치 보너스 배율", example = "0.05")
            @DecimalMin(value = "0.01", message = "보너스 비율은 최소 0.01 이상이어야 합니다.")
            Double expBonusRatio,

            @Schema(description = "포인트 보너스 배율", example = "0.06")
            @DecimalMin(value = "0.01", message = "보너스 비율은 최소 0.01 이상이어야 합니다.")
            Double pointBonusRatio,

            @Schema(description = "칭호 획득 방법 및 설명", example = "일주일 당 42.195 km 씩 1달 동안 달리면 얻을 수 있는 칭호이다.\n" +
                    "꾸준한 사람만이 가질 수 있달까? 이 정도면 나름 러너라고 당당하게 이야기하고 다니자!")
            @Size(min = 1, max = 150, message = "칭호 획득 방법 및 설명은 1~150 글자 이내이어야 합니다.")
            String description
    ) {
    }
}
