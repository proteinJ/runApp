package com.running.runapp.domain.groupRunning.domain;

import com.running.runapp.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SoftDelete(columnName = "isDeleted", strategy = SoftDeleteType.DELETED)
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

    private LocalDateTime joinedAt;

    @Builder
    public GroupMember(GroupRunning groupRunning, Member member) {
        this.groupRunning = groupRunning;
        this.member = member;
        this.joinedAt = LocalDateTime.now();
    }


}