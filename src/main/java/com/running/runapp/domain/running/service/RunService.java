// RunService.java
package com.running.runapp.domain.running.service;

import com.running.runapp.domain.member.domain.Member;
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

import java.time.Duration;
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
    public RunResponse.RunFinishResponse finish(Long memberId, Long runId, RunRequest.RunFinishRequest request) {
        RunningRecord record = runningRecordRepository.findByIdAndMember_Id(runId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

        if (record.getStatus().equals(RunStatus.FINISHED)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (request.getPath() == null || request.getPath().size() < 2) {
            record.finish(request.getEndTime(), 0.0, null);
            return RunResponse.RunFinishResponse.builder()
                    .runId(record.getId())
                    .totalDistanceKm(0.0)
                    .earnedPoints(0)
                    .build();
        }

        LineString lineString = GeometryUtils.toLineString(request.getPath());

        // meter로 계산
        double calculatedDistanceMeter = DistanceUtils.totalDistanceMeter(request);

        record.finish(request.getEndTime(), calculatedDistanceMeter, lineString);

        Integer earnedPoints = spotVisitLogRepository
                .sumEarnedPointsByRunIdAndMemberId(record.getId(), memberId);

        return RunResponse.RunFinishResponse.builder()
                .runId(record.getId())
                .totalDistanceKm(UnitUtils.metersToKm(record.getTotalDistance()))
                .earnedPoints(earnedPoints == null ? 0 : earnedPoints)
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

            if (r.getStartTime() != null && r.getEndTime() != null) {
                long sec = Duration.between(r.getStartTime(), r.getEndTime()).getSeconds();
                if (sec <= 0) continue;

                double km = distMeter / 1000.0;
                if (km >= MIN_PACE_DISTANCE_KM) {
                    paceSum += (sec / km); // sec/km
                    paceCount++;
                }
            }
        }

        double avgDistanceMeter = (totalRuns == 0) ? 0.0 : (totalDistanceMeter / totalRuns);

        // ✅ 페이스: 없으면 null
        Integer avgPaceSecPerKm = (paceCount == 0) ? null : (int) Math.round(paceSum / paceCount);
        String avgPaceText = UnitUtils.secondsToPaceText(avgPaceSecPerKm);

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
                .avgPaceSecPerKm(avgPaceSecPerKm)
                .avgPaceText(avgPaceText)
                .earnedPoints(earnedPoints == null ? 0 : earnedPoints)
                .build();
    }
}