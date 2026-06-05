package com.running.runapp.domain.running;

import com.running.runapp.domain.running.domain.RunStatus;
import com.running.runapp.domain.running.repository.RunningRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RunningRecordScheduler {

    // 3시간 이상 RUNNING 상태로 방치된 기록을 고아(orphan) 레코드로 판단
    private static final long ORPHAN_THRESHOLD_HOURS = 3;

    private final RunningRecordRepository runningRecordRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void deleteOrphanRunningRecords() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(ORPHAN_THRESHOLD_HOURS);
        int deleted = runningRecordRepository.deleteByStatusAndStartTimeBefore(RunStatus.RUNNING, threshold);
        if (deleted > 0) {
            log.info("Orphan running records deleted: count={}", deleted);
        }
    }
}
