package com.running.runapp.domain.running.domain;

import com.running.runapp.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.LineString;

import java.time.LocalDateTime;

@Entity
@Table(name = "running_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RunningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Postgres: bigint auto increment
    @Column(name = "run_id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(columnDefinition = "geometry(LineString, 4326)")
    private LineString path;

    @Column(name = "total_distance")
    private Double totalDistance;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RunStatus status;

    public static RunningRecord create(Member member, LocalDateTime startTime) {
        return RunningRecord.builder()
                .member(member)
                .status(RunStatus.RUNNING)
                .startTime(startTime)
                .build();
    }

    public void finish(LocalDateTime endTime, Double totalDistance, LineString path) {
        this.endTime = endTime;
        this.totalDistance = totalDistance;
        this.path = path;
        this.status = RunStatus.FINISHED;
    }

    public boolean isFinished() {
        return this.endTime != null;
    }
}