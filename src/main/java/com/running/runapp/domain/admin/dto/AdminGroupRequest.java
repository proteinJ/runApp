package com.running.runapp.domain.admin.dto;

import com.running.runapp.domain.groupRunning.domain.GroupStatus;
import jakarta.validation.constraints.NotNull;

public class AdminGroupRequest {

    public record UpdateStatus(
            @NotNull GroupStatus status
    ) {}
}
