package com.running.runapp.domain.running.repository;

import com.running.runapp.domain.running.domain.RunningRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {

    Optional<RunningRecord> findByIdAndMember_Id(Long runId, Long memberId);

    List<RunningRecord> findByMember_IdOrderByStartTimeDesc(Long memberId);
}
