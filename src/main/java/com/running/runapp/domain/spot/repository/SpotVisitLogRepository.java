package com.running.runapp.domain.spot.repository;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.spot.domain.Spot;
import com.running.runapp.domain.spot.domain.SpotVisitLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpotVisitLogRepository extends JpaRepository<SpotVisitLog, Long> {

    Optional<SpotVisitLog> findFirstByMemberAndSpotOrderByVisitedAtDesc(Member member, Spot spot);

    @Query("""
        SELECT l FROM SpotVisitLog l
        JOIN FETCH l.spot
        WHERE l.member.id = :memberId
          AND l.visitedAt > :cutoff
        ORDER BY l.visitedAt DESC
    """)
    List<SpotVisitLog> findActiveCooldownsByMemberId(
            @Param("memberId") Long memberId,
            @Param("cutoff") LocalDateTime cutoff
    );

    @Query("""
        select coalesce(sum(l.spot.rewardAmount), 0)
        from SpotVisitLog l
        where l.runningRecord.id = :runId
          and l.member.id = :memberId
    """)
    Integer sumEarnedPointsByRunIdAndMemberId(
            @Param("runId") Long runId,
            @Param("memberId") Long memberId
    );

    // 월별 획득 포인트 합
    @Query("""
        select coalesce(sum(l.spot.rewardAmount), 0)
        from SpotVisitLog l
        where l.member.id = :memberId
          and l.visitedAt >= :start
          and l.visitedAt < :end
    """)
    Integer sumEarnedPointsByMemberAndMonth(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 랭킹 조회용 (일단 전체 사용자)

    interface RankingRow {
        Long getMemberId();
        String getNickname();
        Integer getTotalPoints();
    }

    @Query("""
        select
            l.member.id as memberId,
            l.member.profile.nickname as nickname,
            coalesce(sum(l.spot.rewardAmount), 0) as totalPoints
        from SpotVisitLog l
        where l.visitedAt >= :start
          and l.visitedAt < :end
        group by l.member.id, l.member.profile.nickname
        order by coalesce(sum(l.spot.rewardAmount), 0) desc, l.member.id asc
    """)
    List<RankingRow> findPointRanking(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );
}