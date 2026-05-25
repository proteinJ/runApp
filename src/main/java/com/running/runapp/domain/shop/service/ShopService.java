package com.running.runapp.domain.shop.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.repository.ProfileRepository;
import com.running.runapp.domain.shop.domain.ShopItem;
import com.running.runapp.domain.shop.domain.ShopPurchase;
import com.running.runapp.domain.shop.dto.ShopRequest;
import com.running.runapp.domain.shop.dto.ShopResponse;
import com.running.runapp.domain.shop.repository.ShopItemRepository;
import com.running.runapp.domain.shop.repository.ShopPurchaseRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final ShopPurchaseRepository shopPurchaseRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public ShopResponse.ItemList listItems(ShopRequest.ItemListQuery query) {
        boolean onlyActive = query != null && Boolean.TRUE.equals(query.onlyActive());

        List<ShopItem> items = onlyActive
                ? shopItemRepository.findByActiveTrueOrderByIdAsc()
                : shopItemRepository.findAll();

        List<ShopResponse.Item> dto = items.stream()
                .map(i -> ShopResponse.Item.builder()
                        .itemId(i.getId())
                        .code(i.getCode())
                        .name(i.getName())
                        .price(i.getPrice())
                        .active(i.getActive())
                        .build()
                )
                .toList();

        return ShopResponse.ItemList.builder().items(dto).build();
    }

    public ShopResponse.PurchaseResult purchase(Member me, ShopRequest.Purchase request) {
        Member member = memberRepository.findById(me.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Profile profile = profileRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        ShopItem item = shopItemRepository.findById(request.itemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_ITEM_NOT_FOUND));

        if (!Boolean.TRUE.equals(item.getActive())) {
            throw new BusinessException(ErrorCode.SHOP_ITEM_INACTIVE);
        }

        int price = item.getPrice();
        int current = profile.getTotalPoint() == null ? 0 : profile.getTotalPoint();

        if (current < price) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINTS);
        }

        profile.addPointAmount(-price);

        ShopPurchase saved = shopPurchaseRepository.save(
                ShopPurchase.create(member, item, price)
        );

        return ShopResponse.PurchaseResult.builder()
                .purchaseId(saved.getId())
                .itemId(item.getId())
                .itemName(item.getName())
                .paidPoints(price)
                .currentTotalPoints(profile.getTotalPoint())
                .purchasedAt(saved.getPurchasedAt())
                .build();
    }
}