package com.running.runapp.domain.running.domain;

import com.running.runapp.domain.member.domain.Member; // 친구가 만든 Member 가져오기
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Bridge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class MemberTagBridge {

    @Id
    @Column(name = "bridge_id")
    private String id;

    // 첫 번째 연결: 누가? (회원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 두 번째 연결: 무엇을? (태그)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private RunningTag runningTag;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}