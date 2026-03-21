package com.running.runapp.domain.spot.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.point.PointHistory;
import com.running.runapp.domain.point.PointHistoryRepository;
import com.running.runapp.domain.running.domain.RunStatus;
import com.running.runapp.domain.running.domain.RunningRecord;
import com.running.runapp.domain.running.repository.RunningRecordRepository;
import com.running.runapp.domain.spot.domain.Spot;
import com.running.runapp.domain.spot.domain.SpotVisitLog;
import com.running.runapp.domain.spot.dto.*;
import com.running.runapp.domain.spot.repository.SpotRepository;
import com.running.runapp.domain.spot.repository.SpotVisitLogRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.running.runapp.global.common.LocationUtils.calculateDistance;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotService {

    private final SpotRepository spotRepository;
    private final GeometryFactory geometryFactory;
    private final MemberRepository memberRepository;
    private final RunningRecordRepository runningRecordRepository;
    private final SpotVisitLogRepository spotVisitLogRepository;
    private final PointHistoryRepository pointHistoryRepository;


    /**
     * Spot 생성
     */
    @Transactional
    public Long createSpot(SpotRequest.SpotCreateRequest dto) {
        Point point = geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude()));

        if (spotRepository.existsByName(dto.name())) {
            throw new BusinessException(ErrorCode.SPOT_NAME_DUPLICATION);
        }

        Spot spot = Spot.builder()
                .name(dto.name())
                .description(dto.description())
                .rewardAmount(dto.rewardAmount())
                .location(point)
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .build();

        return spotRepository.save(spot).getId();
    }

    /**
     * Spot 삭제
     */
    @Transactional
    public Long deleteSpot(Long spotId) {
        Spot spot = findSpotById(spotId);

        spotRepository.delete(spot);

        return spotId;
    }

    /**
     * Spot 수정
     */
    @Transactional
    public Long updateSpot(Long spotId, SpotRequest.SpotUpdateRequest dto) {

        Spot spot = findSpotById(spotId);

        Point newLocation = null;

        if (dto.longitude() != null && dto.latitude() != null) {
            newLocation = geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude()));
        }

        spot.update(dto, newLocation);

        return spot.getId();
    }

    /**
     * Spot 상세 정보
     */
    @Transactional(readOnly = true)
    public SpotResponse.DetailInfo spotInfoResponse(Long spotId) {
        Spot spot = findSpotById(spotId);

        return new SpotResponse.DetailInfo(
                spot.getId(),
                spot.getName(),
                spot.getDescription(),
                spot.getRewardAmount(),
                spot.getLatitude(), // 위도
                spot.getLongitude() // 경도
        );
    }


    /**
     * Spot 체크인
     */
    @Transactional
    public SpotResponse.SpotCheckinResponse spotCheckin(Long spotId, SpotRequest.SpotCheckinRequest dto, String email) {
        // [ 기본 Entity 조회 ]
        // Spot 찾기 (아래 편의메서드 사용)
        Spot spot = findSpotById(spotId);

        // Member 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 전달받은 DTO안의 runId로  러닝 기록 조회 및 검증
        RunningRecord runningRecord = runningRecordRepository.findById(dto.runId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_RECORD_NOT_FOUND));



        // ################## 🚩 1. [ 러닝 기록 검증 ] ########################
        // 소유권 확인: 이 러닝 기록이 지금 요청을 보낸 Member의 것이 맞는가?
        if (!runningRecord.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ErrorCode.INVALID_RECORD_OWNER);
        }

        // 상태 확인: 이 기록의 상태가 여전히 RUNNING(진행 중)인가?
        if (runningRecord.getStatus() != RunStatus.RUNNING) {
            throw new BusinessException(ErrorCode.NOT_RUNNING_STATUS);
        }

        // 시간 확인: 너무 오래된 기록(예: 어제 시작하고 안 끈 기록) - Fail

        // 사용자가 이미 해당 Spot을 CheckIn한 경우(24시간 쿨타임) - Fail
        pointHistoryRepository.findTopByMemberAndSpotAndTypeOrderByCreatedAtDesc(
                member, spot, PointHistory.PointType.EARN
        ).ifPresent(lastHistory -> {
            LocalDateTime lastCheckinTime = lastHistory.getCreatedAt();
            if (lastCheckinTime.isAfter(LocalDateTime.now().minusHours(24))) {
                throw new BusinessException(ErrorCode.DUPLICATE_CHECKIN);
            }
        });


        // ################## 🚩 2. [ 러닝 기록 검증 ] ########################
        spotVisitLogRepository.findFirstByMemberAndSpotOrderByVisitedAtDesc(member, spot).ifPresent(lastVisitLog -> {
            LocalDateTime lastCheckinTime = lastVisitLog.getVisitedAt();
            if (lastCheckinTime.isAfter(LocalDateTime.now().minusHours(24))) {
                throw new BusinessException(ErrorCode.DUPLICATE_CHECKIN);
            }
        });


        // #################### 🚩 3. [ 거리 검증 ] ########################

        log.info("DTO 위도: {}, 경도: {}", dto.latitude(), dto.longitude());
        log.info("스팟 위도: {}, 경도: {}", spot.getLatitude(), spot.getLongitude());

        // LocationUtils에 있는 하버사인 공식을 활용한 거리 계산 메서드(calculateDistance)
        double distance = calculateDistance(
                dto.latitude(), dto.longitude(), spot.getLatitude(), spot.getLongitude()
        );

        if (distance > 30.0) { throw new BusinessException(ErrorCode.OUT_OF_RANGE); }




        // Points(Reward) 지급
        member.addPointAmount(spot.getRewardAmount());

        // Point 획득 기록 생성 (By Builder)
        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .amount(spot.getRewardAmount())
                .type(PointHistory.PointType.EARN)
                .description(spot.getName() + " 방문 체크인")
                .createdAt(dto.timestamp())
                .build();

        // 방문 기록 생성 (By Constructor)
        SpotVisitLog spotVisitLog = new SpotVisitLog(
                spot,
                member,
                runningRecord,
                LocalDateTime.now()
        );


        // DB에 저장
         pointHistoryRepository.save(pointHistory);
         spotVisitLogRepository.save(spotVisitLog);


        // 반환 dto 생성 및 반환
        return SpotResponse.SpotCheckinResponse.of(spot, pointHistory, member);
    }


    /**
     * 내 주변 Spot 조회
     */
    @Transactional(readOnly = true)
    public List<SpotResponse.SummaryInfo> getNearbySpots(SpotRequest.NearbySpotsRequest dto, Long memberId) {
        // 반경이 넘어오지 않으면 기본 1km(1000m) 설정
        double searchRadius = (dto.radius() != null) ? dto.radius() : 1000.0;

        // 1. Native Query 호출 (결과는 List<Object[]>)
        List<Object[]> results = spotRepository.findNearbySpotsNative(
                memberId,
                dto.longitude(), // 🚩 주의: 쿼리 순서에 맞춰 경도(lng) 먼저
                dto.latitude(),  // 위도(lat)
                searchRadius
        );

        // 2. Object[]를 SummaryInfo DTO로 변환
        return results.stream()
                .map(row -> new SpotResponse.SummaryInfo(
                        ((Number) row[0]).longValue(),      // spot_id
                        (String) row[1],                    // name
                        (int) ((Number) row[2]).longValue(),      // rewardAmount
                        ((Number) row[3]).doubleValue(),    // latitude
                        ((Number) row[4]).doubleValue(),    // longitude
                        (Boolean) row[5]                    // isVisited (visited)
                ))
                .collect(Collectors.toList());
    }



    /**
     * 편의 메서드
     */
    // spotId로 Spot 찾기 (예외처리 포함)
    private Spot findSpotById(Long spotId) {
        return spotRepository.findById(spotId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_NOT_FOUND));
    }
}
