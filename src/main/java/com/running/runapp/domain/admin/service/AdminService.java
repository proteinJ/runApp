package com.running.runapp.domain.admin.service;

import com.running.runapp.domain.admin.dto.AdminMemberResponse;
import com.running.runapp.domain.admin.dto.AdminSpotRequest;
import com.running.runapp.domain.admin.dto.AdminSpotResponse;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.spot.domain.Spot;
import com.running.runapp.domain.spot.repository.SpotRepository;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final MemberRepository memberRepository;
    private final SpotRepository spotRepository;
    private final GeometryFactory geometryFactory;

    // ===================== Member =====================

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
}