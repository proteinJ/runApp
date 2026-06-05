package com.running.runapp.domain.running.domain;

import com.running.runapp.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.LineString;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "running_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RunningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // 💡 평균 페이스 컬럼 추가 (1km당 걸린 분 수)
    @Column(name = "avg_pace")
    private Double avgPace;

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

    public void finish(LocalDateTime endTime, Double totalDistanceMeter, LineString path, LocalDateTime realStartTime) {
        this.endTime = endTime;
        this.totalDistance = totalDistanceMeter;
        this.path = path;
        this.status = RunStatus.FINISHED;
        this.avgPace = calculatePace(totalDistanceMeter, realStartTime, endTime);
    }

    private Double calculatePace(Double totalDistanceMeter, LocalDateTime realStartTime, LocalDateTime endTime) {
        if (realStartTime == null || endTime == null || totalDistanceMeter == null || totalDistanceMeter <= 0) {
            return 0.0;
        }

        long totalSeconds = Duration.between(realStartTime, endTime).getSeconds();
        if (totalSeconds <= 0) {
            return 0.0;
        }

        double totalMinutes = totalSeconds / 60.0;
        double totalDistanceKm = totalDistanceMeter / 1000.0;
        return totalMinutes / totalDistanceKm;
    }
    
}