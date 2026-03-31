package com.running.runapp.domain.groupRunning.domain;

import com.running.runapp.domain.groupRunning.dto.GroupRequest;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.global.common.BaseTimeEntity;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@SoftDelete(columnName = "is_deleted")
@Table(name = "run_groups")
public class GroupRunning extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "run_groups_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull(message = "참여 인원은 필수입니다.")
    @Min(value = 2, message = "최소 2명 이상이어야 합니다.")
    @Max(value = 5, message = "최대 5명까지만 가능합니다.")
    private Integer maxParticipants;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer distance;

    @Enumerated(EnumType.STRING)
    @Setter
    private GroupStatus status; // RECRUITING, RUNNING, COMPLETED, CANCELLED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Member host;

    @Builder.Default
    @OneToMany(mappedBy = "groupRunning")
    private List<GroupMember> participants = new ArrayList<>();

    private String location;

    private String address;


    public void addParticipants(Member member) {
        GroupMember participant = GroupMember.builder()
                .groupRunning(this)
                .member(member)
                .build();

        this.participants.add(participant);
    }

    public void updateInfo(GroupRequest.UpdateExtraRequest dto) {
        if (title != null) this.title = dto.title();
        if (content != null) this.content = dto.content();
        if (startTime != null) this.startTime = dto.startTime();
        if (endTime != null) this.endTime = dto.endTime();
        if (distance != null) this.distance = dto.distance();
        if (address != null) this.address = dto.address();
        if (location != null) this.location = dto.location();
        if (maxParticipants != null) {
            // 현재 참여 인원보다 적게 && 최대 인원보다 많게 수정하려는지 체크 로직 추가
            if (this.maxParticipants < dto.maxParticipants() && dto.maxParticipants() > 2) {
                throw new BusinessException(ErrorCode.INVALID_PARTICIPANTS_COUNT);
            }

            this.maxParticipants = dto.maxParticipants();
        }
    }

    public void verify(Member loginMember) {
        if (!this.host.equals(loginMember)) {
            throw new BusinessException(ErrorCode.NOT_HOST);
        }
    }

    public void cancel() {
        this.status = GroupStatus.CANCELLED;
    }
}
