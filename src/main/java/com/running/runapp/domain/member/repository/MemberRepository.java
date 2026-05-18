package com.running.runapp.domain.member.repository;

import com.running.runapp.domain.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    // 관리자 페이지용 검색 (Email 또는 Profile의 Nickname 검색)
    @Query("select m from Member m " +
            "left join m.profile p " +
            "where m.email like %:keyword% " +
            "or p.nickname like %:keyword%")
    Page<Member> searchByEmailOrNickname(@Param("keyword") String keyword, Pageable pageable);
}