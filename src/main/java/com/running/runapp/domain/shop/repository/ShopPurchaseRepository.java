package com.running.runapp.domain.shop.repository;

import com.running.runapp.domain.shop.domain.ShopPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShopPurchaseRepository extends JpaRepository<ShopPurchase, Long> {

    boolean existsByMemberIdAndItemId(Long memberId, Long itemId);

    @Query("select p.item.id from ShopPurchase p where p.member.id = :memberId")
    List<Long> findPurchasedItemIdsByMemberId(@Param("memberId") Long memberId);
}