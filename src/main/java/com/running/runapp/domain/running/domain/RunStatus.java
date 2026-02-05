package com.running.runapp.domain.running.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RunStatus {
    RUNNING,
    FINISHED,
}