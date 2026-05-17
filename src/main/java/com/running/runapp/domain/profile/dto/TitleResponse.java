package com.running.runapp.domain.profile.dto;

import com.running.runapp.domain.profile.domain.Rarity;
import io.swagger.v3.oas.annotations.media.Schema;

public class TitleResponse {

    @Schema
    public record allTitle(
            Long id,
            String name,
            String titleCode,
            Rarity rarity,
            Double expBonusRatio,
            Double pointBonusRatio,
            String description
    ) {}
}
