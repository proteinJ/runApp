package com.running.runapp.domain.running;

import com.running.runapp.domain.member.domain.Member; // 친구가 만든 Member 가져오기
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString; // ⚠️ 지도의 선(경로)을 그리는 도구
import java.time.LocalDateTime;

@Entity
@Table(name = "running_record") // DB 테이블 이름과 맞추기
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningRecord {

    @Id
    @Column(name = "run_id")
    private String runId;

    // 누가 뛰었니? (회원과 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // ⚠️ 중요: 달린 경로 (지도 위에 그려진 선)
    @Column(columnDefinition = "geometry(LineString, 4326)")
    private LineString path;

    @Column(name = "total_distance")
    private Double totalDistance;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;
}