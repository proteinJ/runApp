package com.running.runapp.domain.shop.repository;

import com.running.runapp.domain.shop.domain.ShopPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShopPurchaseRepository extends JpaRepository<ShopPurchase, Long> {

    // 내가 구매한 itemId 목록
    @Query("""
        select p.item.id
        from ShopPurchase p
        where p.member.id = :memberId
    """)
    List<Long> findPurchasedItemIdsByMemberId(@Param("memberId") Long memberId);

    // 내가 이 itemId를 구매했는지 여부
    @Query("""
        select (count(p) > 0)
        from ShopPurchase p
        where p.member.id = :memberId
          and p.item.id = :itemId
    """)
    boolean existsByMemberIdAndItemId(
            @Param("memberId") Long memberId,
            @Param("itemId") Long itemId
    );
}