package com.running.runapp.domain.shop.domain;

import com.running.runapp.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shop_purchase")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ShopPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ShopItem item;

    @Column(nullable = false)
    private Integer paidPoints; // 실제 결제 포인트

    @Column(nullable = false)
    private LocalDateTime purchasedAt;

    public static ShopPurchase create(Member member, ShopItem item, Integer paidPoints) {
        return ShopPurchase.builder()
                .member(member)
                .item(item)
                .paidPoints(paidPoints)
                .purchasedAt(LocalDateTime.now())
                .build();
    }
}