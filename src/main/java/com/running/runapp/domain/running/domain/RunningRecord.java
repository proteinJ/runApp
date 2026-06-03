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

    // 종료 메서드 실행 시 페이스를 '자동'으로 계산하도록 업데이트
    public void finish(LocalDateTime endTime, Double totalDistanceMeter, LineString path) {
        this.endTime = endTime;
        this.totalDistance = totalDistanceMeter;
        this.path = path;
        this.status = RunStatus.FINISHED;

        // 평균 페이스 자동 계산 로직 적용
        this.avgPace = calculatePace(totalDistanceMeter);
    }

    // 내부 헬퍼 메서드로 페이스 계산 (엔티티 밖으로 로직이 새지 않게 보호)
    private Double calculatePace(Double totalDistanceMeter) {
        if (this.startTime == null || this.endTime == null || totalDistanceMeter == null || totalDistanceMeter <= 0) {
            return 0.0;
        }

        // 1. 걸린 시간(초) 계산
        long totalSeconds = Duration.between(this.startTime, this.endTime).getSeconds();

        // 2. 초 단위를 '분' 단위 실수로 변경 (예: 90초 -> 1.5분)
        double totalMinutes = totalSeconds / 60.0;

        // 3. 미터를 킬로미터(km)로 변경
        double totalDistanceKm = totalDistanceMeter / 1000.0;

        // 4. 페이스 = 분 / km
        return totalMinutes / totalDistanceKm;
    }
    
}