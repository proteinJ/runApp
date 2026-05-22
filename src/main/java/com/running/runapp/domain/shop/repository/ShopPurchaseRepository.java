package com.running.runapp.domain.shop.repository;

import com.running.runapp.domain.shop.domain.ShopPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopPurchaseRepository extends JpaRepository<ShopPurchase, Long> {
}