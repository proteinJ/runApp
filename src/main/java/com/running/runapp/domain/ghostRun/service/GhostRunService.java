package com.running.runapp.domain.ghostRun.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.ghostRun.domain.GhostCategory;
import com.running.runapp.domain.ghostRun.domain.GhostRanking;
import com.running.runapp.domain.ghostRun.dto.GhostRequest;
import com.running.runapp.domain.ghostRun.dto.GhostResponse;
import com.running.runapp.domain.ghostRun.repository.GhostRankingRepository;
import com.running.runapp.domain.running.domain.RunningRecord;
import com.running.runapp.domain.running.dto.LatLng;
import com.running.runapp.domain.running.dto.RunResponse;
import com.running.runapp.domain.running.repository.RunningRecordRepository;
import com.running.runapp.domain.running.service.RunService;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.running.runapp.global.common.LocationUtils.calculateDistance;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GhostRunService {

    private static final double GHOST_START_THRESHOLD_METER = 10.0;

    private final GhostRankingRepository ghostRankingRepository;
    private final RunningRecordRepository runningRecordRepository;
    private final GoogleGeocodingService googleGeocodingService;
    private final RunService runService;

    public GhostResponse.GhostRankingListResponse rankings(Double lat, Double lng, GhostCategory category) {
        String addressDong = googleGeocodingService.resolveDong(lat, lng);
        List<GhostResponse.GhostRankingSummary> rankings = ghostRankingRepository.findRankings(addressDong, category)
                .stream()
                .map(GhostResponse.GhostRankingSummary::from)
                .toList();

        return GhostResponse.GhostRankingListResponse.builder()
                .addressDong(addressDong)
                .category(category)
                .rankings(rankings)
                .build();
    }

    public GhostResponse.GhostRankingDetailResponse detail(Long rankingId) {
        GhostRanking ranking = findRanking(rankingId);
        return GhostResponse.GhostRankingDetailResponse.from(ranking);
    }

    @Transactional
    public GhostResponse.GhostRunStartResponse start(Member member, GhostRequest.GhostRunStartRequest request) {
        GhostRanking ranking = findRanking(request.getGhostRankingId());
        double distanceMeter = calculateDistance(
                request.getCurrentLat(),
                request.getCurrentLng(),
                ranking.getStartLat(),
                ranking.getStartLng()
        );

        if (distanceMeter > GHOST_START_THRESHOLD_METER) {
            throw new BusinessException(ErrorCode.GHOST_START_LOCATION_TOO_FAR);
        }

        RunResponse.RunStartResponse run = runService.start(member, request.toRunStartRequest());
        RunningRecord targetRecord = ranking.getRunningRecord();

        return GhostResponse.GhostRunStartResponse.builder()
                .runId(run.getRunId())
                .ghostRankingId(ranking.getId())
                .targetRecordId(targetRecord.getId())
                .targetNickname(targetRecord.getMember().getProfile().getNickname())
                .targetAvgPace(ranking.getAvgPace())
                .build();
    }

    @Transactional
    public GhostResponse.GhostRunFinishResponse finish(
            Member member,
            Long runId,
            GhostRequest.GhostRunFinishRequest request
    ) {
        GhostRanking targetRanking = findRanking(request.getGhostRankingId());
        Double targetAvgPace = targetRanking.getAvgPace();

        RunResponse.RunFinishResponse finishResult = runService.finish(member, runId, request.toRunFinishRequest());
        RunningRecord myRecord = runningRecordRepository.findByIdAndMember_Id(runId, member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_RECORD_NOT_FOUND));

        boolean win = finishResult.getAvgPace() != null
                && targetAvgPace != null
                && finishResult.getAvgPace() < targetAvgPace;

        if (win) {
            LatLng startPoint = request.getPath().get(0);
            targetRanking.replaceRecord(myRecord, finishResult.getAvgPace(), startPoint.lat(), startPoint.lng());
        }

        return GhostResponse.GhostRunFinishResponse.builder()
                .runId(runId)
                .result(win ? "WIN" : "LOSE")
                .myAvgPace(finishResult.getAvgPace())
                .targetAvgPace(targetAvgPace)
                .paceDiff(finishResult.getAvgPace() - targetAvgPace)
                .rankingUpdated(win)
                .build();
    }

    private GhostRanking findRanking(Long rankingId) {
        return ghostRankingRepository.findDetailById(rankingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GHOST_RANKING_NOT_FOUND));
    }
}
