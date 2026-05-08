package com.running.runapp.domain.ranking.service;

import com.running.runapp.domain.ranking.dto.RankingResponse;
import com.running.runapp.domain.spot.repository.SpotVisitLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final SpotVisitLogRepository spotVisitLogRepository;

    // 기본 TOP N
    private static final int DEFAULT_LIMIT = 50;

    public RankingResponse.Result weeklyRanking(Integer limit) {
        int topN = (limit == null || limit <= 0) ? DEFAULT_LIMIT : limit;

        // 월요일 시작 (ISO 기준)
        LocalDate today = LocalDate.now();
        LocalDate weekStartDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEndDate = weekStartDate.plusDays(7);

        LocalDateTime start = weekStartDate.atStartOfDay();
        LocalDateTime end = weekEndDate.atStartOfDay(); // exclusive

        return buildRanking("WEEKLY", start, end, topN);
    }

    public RankingResponse.Result monthlyRanking(Integer year, Integer month, Integer limit) {
        int topN = (limit == null || limit <= 0) ? DEFAULT_LIMIT : limit;

        YearMonth ym;
        if (year == null || month == null) {
            ym = YearMonth.now();
        } else {
            ym = YearMonth.of(year, month);
        }

        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay(); // exclusive

        return buildRanking("MONTHLY", start, end, topN);
    }

    private RankingResponse.Result buildRanking(String type, LocalDateTime start, LocalDateTime end, int topN) {
        List<SpotVisitLogRepository.RankingRow> rows =
                spotVisitLogRepository.findPointRanking(start, end, PageRequest.of(0, topN));

        List<RankingResponse.Item> items = new ArrayList<>();
        int rank = 1;
        for (SpotVisitLogRepository.RankingRow row : rows) {
            items.add(RankingResponse.Item.builder()
                    .rank(rank++)
                    .memberId(row.getMemberId())
                    .nickname(row.getNickname())
                    .totalPoints(row.getTotalPoints() == null ? 0 : row.getTotalPoints())
                    .build());
        }

        return RankingResponse.Result.builder()
                .type(type)
                .periodStart(start)
                .periodEnd(end)
                .items(items)
                .build();
    }
}