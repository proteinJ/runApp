package com.running.runapp.domain.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AdminSpotRequest {

    public record Create(
            @NotBlank String name,
            String description,
            String imageUrl,
            @Min(0) @Max(10000) Integer rewardAmount,
            @Min(0) @Max(200) Long expAmount,
            double latitude,
            double longitude
    ) {}

    public record Update(
            String name,
            String description,
            String imageUrl,
            @Min(0) @Max(10000) Integer rewardAmount,
            Double latitude,
            Double longitude
    ) {}
}
