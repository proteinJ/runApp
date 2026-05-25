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
        seed("CHAR_RED", "빨간색 캐릭터", 1000);
        seed("CHAR_YELLOW", "노란색 캐릭터", 1000);
        seed("CHAR_BLUE", "파란색 캐릭터", 1000);
    }

    private void seed(String code, String name, int price) {
        shopItemRepository.findByCode(code).orElseGet(() ->
                shopItemRepository.save(
                        ShopItem.builder()
                                .code(code)
                                .name(name)
                                .price(price)
                                .active(true)
                                .build()
                )
        );
    }
}