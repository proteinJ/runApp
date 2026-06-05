package com.running.runapp.domain.profile.repository;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByNickname(String nickname);

    // 닉네임 키워드 검색 (member, equippedTitle JOIN FETCH로 N+1 방지)
    @Query("select p from Profile p left join fetch p.member left join fetch p.equippedTitle where lower(p.nickname) like lower(concat('%', :nickname, '%'))")
    List<Profile> findByNicknameContainingIgnoreCase(@Param("nickname") String nickname);

    Optional<Profile> findByMember(Member member);

    Optional<Profile> findByMemberId(Long memberId);
}
