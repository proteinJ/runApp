package com.running.runapp.domain.spot.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.running.runapp.domain.spot.domain.QSpot;
import com.running.runapp.domain.spot.domain.QSpotVisitLog;
import com.running.runapp.domain.spot.dto.SpotResponse;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class SpotRepositoryCustomImpl implements SpotRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    QSpot spot = QSpot.spot;
    QSpotVisitLog spotVisitLog = QSpotVisitLog.spotVisitLog;

    @Override
    public List<SpotResponse.SummaryInfo> findNearbyWithCheckInStatus(Long memberId, Double lat, Double lon, Double distance) {

        // 1. ST_Distance 로직을 Template으로 정의 (거리 정렬용)
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class,
                "ST_Distance({0}, CAST(ST_SetSRID(ST_MakePoint({1}, {2}), 4326) AS geography))",
                spot.location, lon, lat);

        // 2. ST_DWithin 로직을 Template으로 정의 (반경 내 필터링용)
        BooleanExpression isWithinDistance = Expressions.booleanTemplate(
                "ST_DWithin({0}, CAST(ST_SetSRID(ST_MakePoint({1}, {2}), 4326) AS geography), {3})",
                spot.location, lon, lat, distance);

        return queryFactory
                .select(Projections.constructor(SpotResponse.SummaryInfo.class,
                        spot.id,
                        spot.name,
                        spot.rewardAmount,
                        spot.latitude,
                        spot.longitude,
                        spotVisitLog.id.isNotNull()
                        ))
                .from(spot)
                .leftJoin(spotVisitLog).on(
                        spotVisitLog.spot.eq(spot),
                        spotVisitLog.member.id.eq(memberId)
                )
                .where(isWithinDistance)
                .orderBy(distanceExpression.asc())
                .fetch();

    }
}
