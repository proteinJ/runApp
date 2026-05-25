package com.running.runapp.domain.myroom.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.myroom.dto.MyRoomRequest;
import com.running.runapp.domain.myroom.dto.MyRoomResponse;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.domain.Title;
import com.running.runapp.domain.shop.domain.ShopItem;
import com.running.runapp.domain.shop.repository.ShopItemRepository;
import com.running.runapp.domain.shop.repository.ShopPurchaseRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class MyRoomService {

    private final MemberRepository memberRepository;
    private final ShopItemRepository shopItemRepository;
    private final ShopPurchaseRepository shopPurchaseRepository;

    private static boolean isCoreColorItem(ShopItem item) {
        return item.getCode() != null && item.getCode().startsWith("CORE_");
    }

    @Transactional(readOnly = true)
    public MyRoomResponse.Result getMyRoom(Member me) {
        Member member = memberRepository.findById(me.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Profile profile = member.getProfile();
        if (profile == null) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND);
        }

        List<ShopItem> coreItems = shopItemRepository.findAll()
                .stream()
                .filter(MyRoomService::isCoreColorItem)
                .toList();

        List<Long> ownedItemIds = shopPurchaseRepository.findPurchasedItemIdsByMemberId(member.getId());
        Set<Long> ownedSet = new HashSet<>(ownedItemIds);

        String currentColorCode = profile.getCoreColorCode();
        if (currentColorCode == null || currentColorCode.isBlank()) {
            currentColorCode = "CORE_BLACK";
        }

        Title equippedTitle = profile.getEquippedTitle();
        String equippedTitleName = (equippedTitle == null) ? null : equippedTitle.getName();

        List<MyRoomResponse.ColorItem> colors = coreItems.stream()
                .map(i -> MyRoomResponse.ColorItem.builder()
                        .code(i.getCode())
                        .name(i.getName())
                        .hex(i.getHexColor())
                        .owned(ownedSet.contains(i.getId()))
                        .build()
                )
                .toList();

        return MyRoomResponse.Result.builder()
                .memberId(member.getId())
                .nickname(profile.getNickname())
                .equippedTitle(equippedTitleName)
                .currentColorCode(currentColorCode)
                .colors(colors)
                .build();
    }

    public MyRoomResponse.ChangeCoreColorResult changeCoreColor(Member me, MyRoomRequest.ChangeCoreColor request) {
        Member member = memberRepository.findById(me.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Profile profile = member.getProfile();
        if (profile == null) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND);
        }

        ShopItem item = shopItemRepository.findByCode(request.colorCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_ITEM_NOT_FOUND));

        if (!isCoreColorItem(item)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        boolean owned = shopPurchaseRepository.existsByMemberIdAndItemId(member.getId(), item.getId());
        if (!owned) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        profile.setCoreColorCode(item.getCode());

        return MyRoomResponse.ChangeCoreColorResult.builder()
                .currentColorCode(item.getCode())
                .build();
    }
}