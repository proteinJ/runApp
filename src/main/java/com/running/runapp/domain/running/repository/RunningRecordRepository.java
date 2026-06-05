package com.running.runapp.domain.running.repository;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.running.domain.RunStatus;
import com.running.runapp.domain.running.domain.RunningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {

    Optional<RunningRecord> findByIdAndMember_Id(Long runId, Long memberId);

    List<RunningRecord> findByMember_IdOrderByStartTimeDesc(Long memberId);

    List<RunningRecord> findTop30ByMember_IdInOrderByStartTimeDesc(List<Long> memberIds);

    // 월별 요약
    @Query("""
        select r
        from RunningRecord r
        where r.member.id = :memberId
          and r.status = :status
          and r.startTime >= :startInclusive
          and r.startTime < :endExclusive
        order by r.startTime desc
    """)
    List<RunningRecord> findMonthlyRecords(
            @Param("memberId") Long memberId,
            @Param("status") RunStatus status,
            @Param("startInclusive") LocalDateTime startInclusive,
            @Param("endExclusive") LocalDateTime endExclusive
    );

    boolean existsByMemberAndStatus(Member member, RunStatus runStatus);

    long countByMember_IdAndStatus(Long memberId, RunStatus status);

    @Modifying
    @Query("delete from RunningRecord r where r.status = :status and r.startTime < :before")
    int deleteByStatusAndStartTimeBefore(@Param("status") RunStatus status, @Param("before") LocalDateTime before);

    @Query(value = """
      SELECT EXISTS (
          SELECT 1 FROM running_record
          WHERE member_id = :memberId
            AND status = 'FINISHED'
            AND total_distance >= 5000
            AND EXTRACT(EPOCH FROM (end_time - start_time)) / (total_distance / 1000.0) <=
  240
      )
  """, nativeQuery = true)
    boolean existsSpeedKingRecord(@Param("memberId") Long memberId);
}