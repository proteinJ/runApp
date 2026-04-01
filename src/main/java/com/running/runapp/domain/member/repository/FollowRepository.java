package com.running.runapp.domain.member.repository;

import com.running.runapp.domain.member.domain.Follow;
import com.running.runapp.domain.member.domain.Follow.FollowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// ✅ 두 번째 제네릭 타입을 Long에서 String으로 변경 (Follow 엔티티의 ID 타입에 맞춤)
public interface FollowRepository extends JpaRepository<Follow, String> {

    boolean existsByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    Optional<Follow> findByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    // ✅ followId 타입을 Long에서 String으로 변경 (에러 해결 핵심!)
    Optional<Follow> findByIdAndFollowing_Id(String followId, Long followingId);

    @Query("""
        select f from Follow f
        where (f.follower.id = :me and f.following.id = :other)
           or (f.follower.id = :other and f.following.id = :me)
    """)
    List<Follow> findRelationBetween(@Param("me") Long me, @Param("other") Long other);

    // 친구 목록 조회
    @Query("""
        select distinct
            case
                when f.follower.id = :me then f.following.id
                else f.follower.id
            end
        from Follow f
        where f.status = :status
          and (f.follower.id = :me or f.following.id = :me)
    """)
    List<Long> findFriendIds(@Param("me") Long me, @Param("status") FollowStatus status);
}