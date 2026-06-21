package com.running.runapp.domain.ghostRun.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(description = "고스트런 거리 부문", allowableValues = {"1K", "3K", "5K", "10K"})
public enum GhostCategory {
    ONE_K("1K"),
    THREE_K("3K"),
    FIVE_K("5K"),
    TEN_K("10K");

    private final String code;

    GhostCategory(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static GhostCategory from(String value) {
        return Arrays.stream(values())
                .filter(category -> category.code.equalsIgnoreCase(value) || category.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 고스트런 부문입니다: " + value));
    }
}
