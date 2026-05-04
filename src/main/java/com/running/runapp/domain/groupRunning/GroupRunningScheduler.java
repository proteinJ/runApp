package com.running.runapp.domain.groupRunning;

import com.running.runapp.domain.groupRunning.repository.GroupRunningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class GroupRunningScheduler {
    private final GroupRunningRepository groupRunningRepository;

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void autoUpdateStatus() {
        LocalDateTime now = LocalDateTime.now();

        groupRunningRepository.bulkUpdateStatusToRunning(now);
        groupRunningRepository.bulkUpdateStatusToEnded(now);
    }

}
