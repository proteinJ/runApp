package com.running.runapp.domain.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ShopRequest", description = "상점 API 요청 DTO")
public class ShopRequest {

    // GET /api/v1/shop/items?onlyActive=true
    public record ItemListQuery(
            @Schema(description = "판매중(active) 상품만 조회 여부", example = "true")
            Boolean onlyActive
    ) {}

    // POST /api/v1/shop/purchases
    public record Purchase(
            @NotNull
            @Schema(description = "구매할 상품 ID", example = "1")
            Long itemId
    ) {}
}