package com.running.runapp.domain.spot.domain;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.running.domain.RunningRecord;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "spot_visit_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpotVisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "log_id")
    private String id;

    // 1. 어떤 스팟에 갔는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", nullable = false)
    private Spot spot;

    // 2. 누가 방문했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 3. 어떤 러닝 기록 중에 방문했는지 (러닝 기록이 있어야 방문 인정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private RunningRecord runningRecord;

    // 언제 방문했는지
    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    // 생성자 (방문 기록은 생성할 때 바로 값을 넣는 게 좋음)
    public SpotVisitLog(Spot spot, Member member, RunningRecord runningRecord, LocalDateTime visitedAt) {
        this.spot = spot;
        this.member = member;
        this.runningRecord = runningRecord;
        this.visitedAt = visitedAt;
    }
}