package com.running.runapp.domain.groupRunning.domain;

import com.running.runapp.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SoftDelete(columnName = "is_deleted")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_running_id")
    private GroupRunning groupRunning;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Setter
    private GroupRole role; // HOST, PARTICIPANT

    private LocalDateTime joinedAt;

    @Builder
    public GroupMember(GroupRunning groupRunning, Member member, GroupRole role) {
        this.groupRunning = groupRunning;
        this.member = member;
        if (role == null) { this.role = GroupRole.PARTICIPANT; } else { this.role = role; }
        this.joinedAt = LocalDateTime.now();
    }


}