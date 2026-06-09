package com.running.runapp.domain.running.service;

 import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.service.TitleService;
import com.running.runapp.domain.running.domain.RunStatus;
import com.running.runapp.domain.running.domain.RunningRecord;
import com.running.runapp.domain.running.dto.RunRequest;
import com.running.runapp.domain.running.dto.RunResponse;
import com.running.runapp.domain.running.dto.UnitUtils;
import com.running.runapp.domain.running.repository.RunningRecordRepository;
import com.running.runapp.domain.running.util.DistanceUtils;
import com.running.runapp.domain.running.util.GeometryUtils;
import com.running.runapp.domain.spot.repository.SpotVisitLogRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RunService {

    private final RunningRecordRepository runningRecordRepository;
    private final SpotVisitLogRepository spotVisitLogRepository;
    private final TitleService titleService;

    // ✅ 러닝 시작
    @Transactional
    public RunResponse.RunStartResponse start(Member member, RunRequest.RunStartRequest request) {
        RunningRecord record = RunningRecord.create(member, request.getStartTime());
        runningRecordRepository.save(record);

        log.info("Run start: memberId={}, runId={}", member.getId(), record.getId());

        return RunResponse.RunStartResponse.builder()
                .runId(record.getId())
                .memberId(member.getId())
                .build();
    }

    // ✅ 러닝 종료
    @Transactional
    public RunResponse.RunFinishResponse finish(Member member, Long runId, RunRequest.RunFinishRequest request) {
        RunningRecord record = runningRecordRepository.findByIdAndMember_Id(runId, member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

        if (record.getStatus().equals(RunStatus.FINISHED)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (request.getPath() == null || request.getPath().size() < 2) {
            runningRecordRepository.delete(record);
            log.warn("Run record deleted - insufficient path: memberId={}, runId={}", member.getId(), runId);
            throw new BusinessException(ErrorCode.RUN_DISTANCE_TOO_SHORT);
        }

        double calculatedDistanceMeter = DistanceUtils.totalDistanceMeter(request);
        if (calculatedDistanceMeter < 150.0) {
            runningRecordRepository.delete(record);
            log.warn("Run record deleted - distance too short: memberId={}, runId={}, distanceM={}", member.getId(), runId, calculatedDistanceMeter);
            throw new BusinessException(ErrorCode.RUN_DISTANCE_TOO_SHORT);
        }

        LineString lineString = GeometryUtils.toLineString(request.getPath());
        record.finish(request.getEndTime(), calculatedDistanceMeter, lineString, request.getRealStartTime());

        long finishedCount = runningRecordRepository.countByMember_IdAndStatus(member.getId(), RunStatus.FINISHED);
        member.getProfile().updateRunStats(calculatedDistanceMeter, record.getAvgPace(), finishedCount);

        Integer earnedPoints = spotVisitLogRepository
                .sumEarnedPointsByRunIdAndMemberId(record.getId(), member.getId());

        // 칭호 지급 조건 확인 및 처리
        titleService.checkAndGrantTitles(member.getProfile());

        log.info("Run finished: memberId={}, runId={}, distanceKm={}", member.getId(), record.getId(), UnitUtils.metersToKm(calculatedDistanceMeter));

        return RunResponse.RunFinishResponse.builder()
                .runId(record.getId())
                .totalDistanceKm(UnitUtils.metersToKm(record.getTotalDistance()))
                .earnedPoints(earnedPoints == null ? 0 : earnedPoints)
                .avgPace(record.getAvgPace())
                .build();
    }

    // ✅ 내 러닝 목록 조회
    @Transactional(readOnly = true)
    public List<RunResponse.MyRunSummaryResponse> myRuns(Member member) {
        return runningRecordRepository.findByMember_IdOrderByStartTimeDesc(member.getId())
                .stream()
                .map(RunResponse.MyRunSummaryResponse::from)
                .toList();
    }

    // ✅ 러닝 상세 조회
    @Transactional(readOnly = true)
    public RunResponse.RunDetailResponse detail(Member member, Long runId) {
        RunningRecord record = runningRecordRepository.findByIdAndMember_Id(runId, member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

        return RunResponse.RunDetailResponse.from(record);
    }

    // ✅ 월별 러닝 요약
    @Transactional(readOnly = true)
    public RunResponse.MonthlySummaryResponse monthlySummary(Member member, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();

        List<RunningRecord> records = runningRecordRepository.findMonthlyRecords(
                member.getId(),
                RunStatus.FINISHED,
                start,
                end
        );

        int totalRuns = records.size();

        double totalDistanceMeter = 0.0;
        double bestDistanceMeter = 0.0;

        // 평균 페이스 (sec/km)
        double paceSum = 0.0;
        int paceCount = 0;

        // ✅ 너무 짧은 러닝은 페이스 계산 제외(실무에서 흔히 넣는 컷)
        final double MIN_PACE_DISTANCE_KM = 0.2;

        for (RunningRecord r : records) {
            Double distMeter = r.getTotalDistance();
            if (distMeter == null || distMeter <= 0) continue;

            totalDistanceMeter += distMeter;
            bestDistanceMeter = Math.max(bestDistanceMeter, distMeter);

            double km = distMeter / 1000.0;
            Double pace = r.getAvgPace(); // min/km
            if (km >= MIN_PACE_DISTANCE_KM && pace != null && pace > 0) {
                paceSum += pace;
                paceCount++;
            }
        }

        double avgDistanceMeter = (totalRuns == 0) ? 0.0 : (totalDistanceMeter / totalRuns);

        Double avgPace = (paceCount == 0) ? null : paceSum / paceCount;

        Integer earnedPoints = spotVisitLogRepository.sumEarnedPointsByMemberAndMonth(
                member.getId(),
                start,
                end
        );

        return RunResponse.MonthlySummaryResponse.builder()
                .year(year)
                .month(month)
                .totalRuns(totalRuns)
                .totalDistanceKm(UnitUtils.metersToKm(totalDistanceMeter))
                .avgDistanceKm(UnitUtils.metersToKm(avgDistanceMeter))
                .bestDistanceKm(UnitUtils.metersToKm(bestDistanceMeter))
                .avgPace(avgPace)
                .earnedPoints(earnedPoints == null ? 0 : earnedPoints)
                .build();
    }
}