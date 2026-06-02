package com.running.runapp.domain.admin.service;

import com.running.runapp.domain.admin.dto.AdminGroupRequest;
import com.running.runapp.domain.admin.dto.AdminGroupResponse;
import com.running.runapp.domain.admin.dto.AdminMemberResponse;
import com.running.runapp.domain.admin.dto.AdminShopRequest;
import com.running.runapp.domain.admin.dto.AdminShopResponse;
import com.running.runapp.domain.admin.dto.AdminSpotRequest;
import com.running.runapp.domain.admin.dto.AdminSpotResponse;
import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import com.running.runapp.domain.groupRunning.repository.GroupRunningRepository;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.domain.Role;
import com.running.runapp.domain.member.dto.MemberRequest;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.domain.ProfileTitle;
import com.running.runapp.domain.profile.domain.Title;
import com.running.runapp.domain.profile.dto.TitleRequest;
import com.running.runapp.domain.profile.dto.TitleResponse;
import com.running.runapp.domain.profile.repository.ProfileRepository;
import com.running.runapp.domain.profile.repository.ProfileTitleRepository;
import com.running.runapp.domain.profile.repository.TitleRepository;
import com.running.runapp.domain.shop.domain.ShopItem;
import com.running.runapp.domain.shop.repository.ShopItemRepository;
import com.running.runapp.domain.spot.domain.Spot;
import com.running.runapp.domain.spot.repository.SpotRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminService {

    private final MemberRepository memberRepository;
    private final SpotRepository spotRepository;
    private final GeometryFactory geometryFactory;
    private final ProfileTitleRepository profileTitleRepository;
    private final TitleRepository titleRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShopItemRepository shopItemRepository;
    private final GroupRunningRepository groupRunningRepository;

    // ===================== Member =====================

    @Transactional
    public Long join(MemberRequest.Join req) {
        validateDuplicateMember(req);

        String encodedPassword = passwordEncoder.encode(req.password());

        Member member = Member.builder()
                .email(req.email())
                .password(encodedPassword)
                .realname(req.realname())
                .role(Role.ADMIN)
                .build();

        Profile profile = Profile.builder()
                .nickname(req.nickname())
                .level(1)
                .totalPoint(0)
                .totalDistance(0.0)
                .coreColorCode("CORE_RAINBOW")
                .avgPace(0.0)
                .build();

        member.setProfile(profile);

        Member savedMember = memberRepository.save(member);

        Title defaultTitle = titleRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ErrorCode.TITLE_NOT_FOUND));

        ProfileTitle grantedTitle = ProfileTitle.grantTitle(profile, defaultTitle);
        profileTitleRepository.save(grantedTitle);

        profile.equipTitle(grantedTitle);

        return savedMember.getId();
    }

    public Page<AdminMemberResponse.Summary> getMembers(String keyword, Pageable pageable) {
        Page<Member> page;
        if (keyword == null || keyword.isBlank()) {
            page = memberRepository.findAll(pageable);
        } else {
            page = memberRepository.searchByEmailOrNickname(keyword, pageable);
        }
        return page.map(AdminMemberResponse.Summary::from);
    }

    public AdminMemberResponse.Detail getMemberDetail(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return AdminMemberResponse.Detail.from(member);
    }

    // ===================== Spot =====================

    public Page<AdminSpotResponse.Summary> getSpots(String keyword, Pageable pageable) {
        Page<Spot> page;
        if (keyword == null || keyword.isBlank()) {
            page = spotRepository.findAll(pageable);
        } else {
            page = spotRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
        return page.map(AdminSpotResponse.Summary::from);
    }

    public AdminSpotResponse.Detail getSpotDetail(Long spotId) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_NOT_FOUND));
        return AdminSpotResponse.Detail.from(spot);
    }

    @Transactional
    public Long createSpot(AdminSpotRequest.Create dto) {
        if (spotRepository.existsByName(dto.name())) {
            throw new BusinessException(ErrorCode.SPOT_NAME_DUPLICATION);
        }

        Point point = geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude()));

        Spot spot = Spot.builder()
                .name(dto.name())
                .description(dto.description())
                .rewardAmount(dto.rewardAmount())
                .location(point)
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .imageUrl(dto.imageUrl())
                .build();

        return spotRepository.save(spot).getId();
    }

    @Transactional
    public Long updateSpot(Long spotId, AdminSpotRequest.Update dto) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_NOT_FOUND));

        Point newLocation = null;
        if (dto.latitude() != null && dto.longitude() != null) {
            newLocation = geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude()));
        }

        // ✅ 개별 Setter 대신 하나의 업데이트 메서드 사용 (Spot 엔티티에 추가 필요)
        spot.updateSpotInfo(
                dto.name(),
                dto.description(),
                dto.rewardAmount(),
                dto.imageUrl(),
                newLocation,
                dto.latitude(),
                dto.longitude()
        );

        return spot.getId();
    }

    @Transactional
    public Long deleteSpot(Long spotId) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_NOT_FOUND));
        spotRepository.delete(spot);
        return spotId;
    }

    // ===================== Shop =====================

    public List<AdminShopResponse.Item> getShopItems() {
        return shopItemRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(AdminShopResponse.Item::from)
                .toList();
    }

    @Transactional
    public AdminShopResponse.Item updateShopItem(Long itemId, AdminShopRequest.UpdateItem dto) {
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_ITEM_NOT_FOUND));

        if (dto.code() != null && !dto.code().equals(item.getCode())
                && shopItemRepository.findByCode(dto.code()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        item.updateItemInfo(
                dto.code(),
                dto.name(),
                dto.hexColor(),
                dto.price(),
                dto.active()
        );

        return AdminShopResponse.Item.from(item);
    }

    @Transactional
    public AdminShopResponse.Item updateShopItemActive(Long itemId, AdminShopRequest.UpdateItemActive dto) {
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_ITEM_NOT_FOUND));

        item.updateActive(dto.active());

        return AdminShopResponse.Item.from(item);
    }

    // ===================== GroupRunning =====================

    @Transactional
    public AdminGroupResponse.StatusResult updateGroupStatus(Long groupId, AdminGroupRequest.UpdateStatus dto) {
        GroupRunning groupRunning = groupRunningRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        groupRunning.setStatus(dto.status());

        return AdminGroupResponse.StatusResult.from(groupRunning);
    }

    // ===================== Title =====================

    @Transactional
    public TitleResponse.TitleInfo addNewTitle(TitleRequest.addNewTitle dto) {
        if (titleRepository.existsByTitleCode(dto.titleCode())) {
            log.warn("칭호 등록 실패 - 이미 존재하는 칭호 코드: {}", dto.titleCode());
            throw new BusinessException(ErrorCode.DUPLICATE_TITLE_CODE);
        }

        Title savedTitle = titleRepository.save(dto.toEntity());

        log.info("새로운 시스템 칭호 등록 완료 - ID: {}, Code: {}, Name: {}",
                savedTitle.getId(), savedTitle.getTitleCode(), savedTitle.getName());

        // 엔티티를 응답 DTO로 변환하여 최종 리턴
        return TitleResponse.TitleInfo.from(savedTitle);
    }

    @Transactional
    public TitleResponse.TitleInfo updateTitleInfo(TitleRequest.updateTitleInfo dto) {
        Title title = findTitleById(dto.id());

        title.updateInfo(
                dto.name(),
                dto.titleCode(),
                dto.rarity(),
                dto.expBonusRatio(),
                dto.pointBonusRatio(),
                dto.description());

        return TitleResponse.TitleInfo.from(title);
    }

    @Transactional
    public void deleteTitle(Long titleId) {
        Title title = findTitleById(titleId);

        log.info("칭호 삭제 완료 - ID: {}", title.getId());

        titleRepository.deleteById(title.getId());
    }

    private Title findTitleById(Long titleId) {
        return titleRepository.findById(titleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TITLE_NOT_FOUND));
    }



    private void validateDuplicateMember(MemberRequest.Join req) {
        if (memberRepository.existsByEmail(req.email())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATION);
        }
        if (profileRepository.findByNickname(req.nickname()).isPresent()) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATION);
        }
    }
}
