package com.running.runapp.domain.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "ShopResponse", description = "상점 API 응답 DTO")
public class ShopResponse {

    @Builder
    public record Item(
            @Schema(description = "상품 ID", example = "1")
            Long itemId,

            @Schema(description = "상품 코드", example = "CHAR_RED")
            String code,

            @Schema(description = "상품명", example = "빨간색 캐릭터")
            String name,

            @Schema(description = "가격(포인트)", example = "1000")
            Integer price,

            @Schema(description = "판매중 여부", example = "true")
            Boolean active
    ) {}

    @Builder
    public record ItemList(
            @Schema(description = "상품 목록")
            List<Item> items
    ) {}

    @Builder
    public record PurchaseResult(
            @Schema(description = "구매 ID", example = "10")
            Long purchaseId,

            @Schema(description = "상품 ID", example = "1")
            Long itemId,

            @Schema(description = "상품명", example = "빨간색 캐릭터")
            String itemName,

            @Schema(description = "지불한 포인트", example = "1000")
            Integer paidPoints,

            @Schema(description = "구매 후 보유 포인트", example = "250")
            Integer currentTotalPoints,

            @Schema(description = "구매 시각", example = "2026-05-14T10:30:00")
            LocalDateTime purchasedAt
    ) {}
}