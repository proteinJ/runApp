package com.running.runapp.domain.groupRunning.repository;

import com.running.runapp.domain.groupRunning.dto.GroupResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GroupRunningRepositoryCustom {
    Slice<GroupResponse.GroupSummary> findAllByFilter(Pageable pageable);
}
