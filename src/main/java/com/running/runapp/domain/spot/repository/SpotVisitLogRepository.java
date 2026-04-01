package com.running.runapp.domain.spot.repository;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.spot.domain.Spot;
import com.running.runapp.domain.spot.domain.SpotVisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpotVisitLogRepository extends JpaRepository<SpotVisitLog, Long> {

    Optional<SpotVisitLog> findFirstByMemberAndSpotOrderByVisitedAtDesc(Member member, Spot spot);

    @Query("""
        select coalesce(sum(l.spot.rewardAmount), 0)
        from SpotVisitLog l
        where l.runningRecord.id = :runId
          and l.member.id = :memberId
    """)
    Integer sumEarnedPointsByRunIdAndMemberId(@Param("runId") Long runId,
                                              @Param("memberId") Long memberId);
}