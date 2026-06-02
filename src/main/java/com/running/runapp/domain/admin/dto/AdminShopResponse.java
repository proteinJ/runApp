package com.running.runapp.domain.admin.dto;

import com.running.runapp.domain.shop.domain.ShopItem;
import lombok.Builder;

public class AdminShopResponse {

    @Builder
    public record Item(
            Long itemId,
            String code,
            String name,
            String hexColor,
            Integer price,
            Boolean active
    ) {
        public static Item from(ShopItem item) {
            return Item.builder()
                    .itemId(item.getId())
                    .code(item.getCode())
                    .name(item.getName())
                    .hexColor(item.getHexColor())
                    .price(item.getPrice())
                    .active(item.getActive())
                    .build();
        }
    }
}
