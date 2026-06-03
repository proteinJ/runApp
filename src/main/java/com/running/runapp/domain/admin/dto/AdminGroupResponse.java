package com.running.runapp.domain.admin.dto;

import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import lombok.Builder;

public class AdminGroupResponse {

    @Builder
    public record StatusResult(
            Long groupId,
            String status
    ) {
        public static StatusResult from(GroupRunning groupRunning) {
            return StatusResult.builder()
                    .groupId(groupRunning.getId())
                    .status(groupRunning.getStatus().name())
                    .build();
        }
    }
}
