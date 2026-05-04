package com.running.runapp.domain.groupRunning.repository;

import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface GroupRunningRepository
        extends JpaRepository<GroupRunning, Long>,
        GroupRunningRepositoryCustom
{
    @Modifying
    @Query("UPDATE GroupRunning g SET g.status = 'RUNNING' " +
            "WHERE g.startTime <= :now AND g.status = 'READY' "
    )
    void bulkUpdateStatusToRunning(LocalDateTime now);


    @Modifying
    @Query("UPDATE GroupRunning g SET g.status = 'COMPLETED' " +
            "WHERE g.startTime <= :now AND g.status = 'RUNNING' "
    )
    void bulkUpdateStatusToEnded(LocalDateTime now);
}
