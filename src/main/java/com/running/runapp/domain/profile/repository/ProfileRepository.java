package com.running.runapp.domain.profile.repository;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByNickname(String nickname);

    // 닉네임 키워드 검색
    List<Profile> findByNicknameContainingIgnoreCase(String nickname);

    Optional<Profile> findByMember(Member member);

    Optional<Profile> findByMemberId(Long memberId);
}
