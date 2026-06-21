package com.running.runapp.domain.ghostRun.domain;

import com.running.runapp.domain.running.domain.RunningRecord;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ghost_ranking",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ghost_ranking_dong_category_rank",
                        columnNames = {"address_dong", "category", "rank_no"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GhostRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ranking_id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "running_record_id", nullable = false)
    private RunningRecord runningRecord;

    @Column(name = "address_dong", nullable = false, length = 100)
    private String addressDong;

    @Convert(converter = GhostCategoryConverter.class)
    @Column(name = "category", nullable = false, length = 10)
    private GhostCategory category;

    @Column(name = "rank_no", nullable = false)
    private Integer rankNo;

    @Column(name = "avg_pace", nullable = false)
    private Double avgPace;

    @Column(name = "start_lat", nullable = false)
    private Double startLat;

    @Column(name = "start_lng", nullable = false)
    private Double startLng;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void replaceRecord(RunningRecord runningRecord, Double avgPace, Double startLat, Double startLng) {
        this.runningRecord = runningRecord;
        this.avgPace = avgPace;
        this.startLat = startLat;
        this.startLng = startLng;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
