package com.running.runapp.domain.groupRunning.repository;

import com.running.runapp.domain.groupRunning.domain.GroupMember;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE group_member SET is_deleted = true WHERE group_running_id = :groupId", nativeQuery = true)
    void bulkSoftDeleteByGroup(@Param("groupId") Long groupId);

