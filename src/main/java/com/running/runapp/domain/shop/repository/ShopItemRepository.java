package com.running.runapp.domain.shop.repository;

import com.running.runapp.domain.shop.domain.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {

    List<ShopItem> findByActiveTrueOrderByIdAsc();

    Optional<ShopItem> findByCode(String code);
}