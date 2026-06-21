package com.running.runapp.domain.ghostRun.repository;

import com.running.runapp.domain.ghostRun.domain.GhostCategory;
import com.running.runapp.domain.ghostRun.domain.GhostRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GhostRankingRepository extends JpaRepository<GhostRanking, Long> {

    @Query("""
        select gr from GhostRanking gr
        join fetch gr.runningRecord rr
        join fetch rr.member m
        join fetch m.profile p
        where gr.addressDong = :addressDong
          and gr.category = :category
        order by gr.rankNo asc
    """)
    List<GhostRanking> findRankings(
            @Param("addressDong") String addressDong,
            @Param("category") GhostCategory category
    );

    @Query("""
        select gr from GhostRanking gr
        join fetch gr.runningRecord rr
        join fetch rr.member m
        join fetch m.profile p
        where gr.id = :rankingId
    """)
    Optional<GhostRanking> findDetailById(@Param("rankingId") Long rankingId);
}
