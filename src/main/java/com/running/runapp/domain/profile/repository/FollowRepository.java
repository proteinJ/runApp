package com.running.runapp.domain.profile.repository;

import com.running.runapp.domain.profile.domain.Follow;
import com.running.runapp.domain.profile.domain.Follow.FollowStatus;
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
        where f.follower.id = :me and f.following.id = :other
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

    // 받은 친구 신청 목록 (PENDING)
    @Query("select f from Follow f join fetch f.follower fr join fetch fr.member where f.following.member.id = :memberId and f.status = 'PENDING'")
    List<Follow> findPendingRequestsByMemberId(@Param("memberId") Long memberId);

    // 나를 팔로우하는 사람들 (팔로워)
    @Query("select f from Follow f join fetch f.follower fr join fetch fr.member where f.following.member.id = :memberId and f.status = 'ACCEPTED'")
    List<Follow> findFollowersByMemberId(@Param("memberId") Long memberId);

    // 내가 팔로우하는 사람들 (팔로잉)
    @Query("select f from Follow f join fetch f.following fg join fetch fg.member where f.follower.member.id = :memberId and f.status = 'ACCEPTED'")
    List<Follow> findFollowingsByMemberId(@Param("memberId") Long memberId);
}
