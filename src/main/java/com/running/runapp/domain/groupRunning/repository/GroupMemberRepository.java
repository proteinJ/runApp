package com.running.runapp.domain.groupRunning.repository;

import com.running.runapp.domain.groupRunning.domain.GroupMember;
import com.running.runapp.domain.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE group_member SET is_deleted = true WHERE group_running_id = :groupId", nativeQuery = true)
    void bulkSoftDeleteByGroup(@Param("groupId") Long groupId);

    @Query("SELECT COUNT(gm) > 0 FROM GroupMember gm " +
            "JOIN gm.groupRunning gr " +
            "WHERE gm.member = :member " +
            "AND (:startTime < gr.endTime AND :endTime > gr.startTime)"
    )
    boolean hasOverlappingSchedule(Member member, LocalDateTime startTime, LocalDateTime endTime);

    boolean existsByGroupRunningIdAndMember(Long groupRunningId, Member member);

    Optional<GroupMember> findByGroupRunningIdAndMember(Long groupRunningId, Member member);

    @Query("SELECT gm FROM GroupMember gm JOIN FETCH gm.member m JOIN FETCH m.profile WHERE gm.groupRunning.id = :groupId")
    List<GroupMember> findMembersWithProfileByGroupId(@Param("groupId") Long groupId);
}

