package com.running.runapp.domain.groupRunning.domain;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SoftDelete(columnName = "is_deleted", strategy = SoftDeleteType.DELETED)
@Table(name = "run_groups")
public class GroupRunning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "run_groups_id")
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
    @Setter
    private GroupStatus status; // RECRUITING, RUNNING, COMPLETED, CANCELLED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Member host;

    @OneToMany(mappedBy = "groupRunning", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<GroupMember> participants = new ArrayList<>();
    
    // 그룹 삭제 시각
    private boolean isDeleted;


    public void addParticipants(GroupMember participant) {
        this.participants.add(participant);
    }

    public void updateInfo(String title, String content, Integer maxParticipants, LocalDateTime startTime) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (startTime != null) this.startTime = startTime;
        if (maxParticipants != null) {
            // 현재 참여 인원보다 적게 수정하려는지 체크 로직 추가 가능
            if (this.maxParticipants < maxParticipants && maxParticipants > 2) {
                throw new BusinessException(ErrorCode.INVALID_PARTICIPANTS_COUNT);
            }

            this.maxParticipants = maxParticipants;
        }
    }

    public void verify(Member loginMember) {
        if (!this.host.equals(loginMember)) {
            throw new BusinessException(ErrorCode.NOT_HOST);
        }
    }
}
