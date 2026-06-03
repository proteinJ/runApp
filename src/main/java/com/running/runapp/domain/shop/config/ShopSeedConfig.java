package com.running.runapp.domain.shop.config;

import com.running.runapp.domain.shop.domain.ShopItem;
import com.running.runapp.domain.shop.repository.ShopItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ShopSeedConfig implements CommandLineRunner {

    private final ShopItemRepository shopItemRepository;

    @Override
    public void run(String... args) {
        seed("CORE_RED", "빨간색 코어", "#E53935", 500);
        seed("CORE_ORANGE", "주황색 코어", "#F57C00", 0);
        seed("CORE_YELLOW", "노란색 코어", "#F9A825", 500);
        seed("CORE_GREEN", "초록색 코어", "#43A047", 500);
        seed("CORE_BLUE", "파란색 코어", "#1E88E5", 500);
        seed("CORE_NAVY", "남색 코어", "#283593", 500);
        seed("CORE_PURPLE", "보라색 코어", "#8E24AA", 500);
        seed("CORE_BLACK", "검은색 코어", "#212121", 500);
    }

    private void seed(String code, String name, String hex, int price) {
        shopItemRepository.findByCode(code).orElseGet(() ->
                shopItemRepository.save(
                        ShopItem.builder()
                                .code(code)
                                .name(name)
                                .hexColor(hex)
                                .price(price)
                                .active(true)
                                .build()
                )
        );
    }
}