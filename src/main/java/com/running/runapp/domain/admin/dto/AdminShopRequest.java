package com.running.runapp.domain.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdminShopRequest {

    public record UpdateItem(
            String code,
            String name,
            String hexColor,
            @Min(0) Integer price,
            Boolean active
    ) {}

    public record UpdateItemActive(
            @NotNull Boolean active
    ) {}
}
