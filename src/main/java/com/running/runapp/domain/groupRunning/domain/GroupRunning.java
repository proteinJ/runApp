package com.running.runapp.domain.groupRunning.domain;

import com.running.runapp.domain.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "run_groups")
public class GroupRunning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_running_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Size(max = 10, min = 1)
    private Integer maxParticipants;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    private GroupStatus status; // RECRUITING, RUNNING, COMPLETED, CANCELLED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Member host;

    @OneToMany(mappedBy = "groupRunning", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> participants = new ArrayList<>();

    public void addParticipants(GroupMember participant) {
        this.participants.add(participant);
    }

    public void updateInfo(String title, String content, Integer maxParticipants, LocalDateTime startTime) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (startTime != null) this.startTime = startTime;
        if (maxParticipants != null) {
            // 현재 참여 인원보다 적게 수정하려는지 체크 로직 추가 가능
            this.maxParticipants = maxParticipants;
        }
    }
}
