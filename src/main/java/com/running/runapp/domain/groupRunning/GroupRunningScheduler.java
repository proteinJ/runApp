package com.running.runapp.domain.groupRunning;

import com.running.runapp.domain.groupRunning.repository.GroupRunningRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class GroupRunningScheduler {
    private final GroupRunningRepository groupRunningRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void autoUpdateStatus() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Scheduler: auto updating group statuses at {}", now);

        groupRunningRepository.bulkUpdateStatusToRunning(now);
        groupRunningRepository.bulkUpdateStatusToEnded(now);
    }

}
