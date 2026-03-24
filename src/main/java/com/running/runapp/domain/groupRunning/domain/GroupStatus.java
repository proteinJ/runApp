package com.running.runapp.domain.groupRunning.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupStatus {
    // 모집중, 러닝중, 완료, 취소
    RECRUITING, RUNNING, COMPLETED, CANCELLED;
}
