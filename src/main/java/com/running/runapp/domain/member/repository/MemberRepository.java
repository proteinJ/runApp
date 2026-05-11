package com.running.runapp.domain.member.repository;

import com.running.runapp.domain.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    // 닉네임 키워드 검색
    List<Member> findByNicknameContainingIgnoreCase(String nickname);

    // 관리자 페이지용 검색
    @Query("select m from Member m where m.email like %:keyword% or m.nickname like %:keyword%")
    Page<Member> searchByEmailOrNickname(@Param("keyword") String keyword, Pageable pageable);
}