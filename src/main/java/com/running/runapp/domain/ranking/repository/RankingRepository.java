package com.running.runapp.domain.ranking.repository;

import com.running.runapp.domain.spot.domain.SpotVisitLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RankingRepository extends JpaRepository<SpotVisitLog, Long> {

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
    List<RankingRow> findRanking(LocalDateTime start, LocalDateTime end, Pageable pageable);
}