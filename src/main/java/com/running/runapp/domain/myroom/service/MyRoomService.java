package com.running.runapp.domain.myroom.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.myroom.dto.MyRoomRequest;
import com.running.runapp.domain.myroom.dto.MyRoomResponse;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.domain.Title;
import com.running.runapp.domain.profile.repository.ProfileTitleRepository;
import com.running.runapp.domain.profile.repository.TitleRepository;
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
    private final TitleRepository titleRepository;
    private final ProfileTitleRepository profileTitleRepository;

    private static boolean isCoreColorItem(ShopItem item) {
        return item.getCode() != null && item.getCode().startsWith("CORE_");
    }

    private static String normalizeCoreColorCode(String colorCode) {
        if (colorCode == null || colorCode.isBlank()) {
            return "CORE_ORANGE";
        }
        if (colorCode.startsWith("CHAR_")) {
            return "CORE_" + colorCode.substring("CHAR_".length());
        }
        return colorCode;
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
        currentColorCode = normalizeCoreColorCode(currentColorCode);

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

        // 전체 칭호 목록 + 보유 여부
        List<Title> allTitles = titleRepository.findAll();
        Set<Long> ownedTitleIds = new HashSet<>(profileTitleRepository.findTitleIdsByProfileId(profile.getId()));

        List<MyRoomResponse.TitleItem> titles = allTitles.stream()
                .map(t -> MyRoomResponse.TitleItem.builder()
                        .titleId(t.getId())
                        .titleCode(t.getTitleCode())
                        .name(t.getName())
                        .rarity(t.getRarity() != null ? t.getRarity().name() : null)
                        .description(t.getDescription())
                        .owned(ownedTitleIds.contains(t.getId()))
                        .build()
                )
                .toList();

        return MyRoomResponse.Result.builder()
                .memberId(member.getId())
                .nickname(profile.getNickname())
                .equippedTitle(equippedTitleName)
                .currentColorCode(currentColorCode)
                .colors(colors)
                .titles(titles)
                .build();
    }

    public MyRoomResponse.ChangeCoreColorResult changeCoreColor(Member me, MyRoomRequest.ChangeCoreColor request) {
        Member member = memberRepository.findById(me.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Profile profile = member.getProfile();
        if (profile == null) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND);
        }

        String colorCode = normalizeCoreColorCode(request.colorCode());

        ShopItem item = shopItemRepository.findByCode(colorCode)
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
