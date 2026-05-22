package com.running.runapp.domain.shop.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ShopItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code; // ex) CHAR_RED

    @Column(nullable = false, length = 100)
    private String name; // ex) 빨간색 캐릭터

    @Column(nullable = false)
    private Integer price; // 포인트 가격

    @Column(nullable = false)
    private Boolean active; // 판매중 여부
}