package com.running.runapp.domain.groupRunning.repository;

import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRunningRepository
        extends JpaRepository<GroupRunning, Long>,
        GroupRunningRepositoryCustom
{
}
