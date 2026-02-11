package com.running.runapp.domain.spot.repository;


import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.spot.domain.Spot;
import com.running.runapp.domain.spot.domain.SpotVisitLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SpotVisitLogRepository extends JpaRepository<SpotVisitLog, Long> {

    Optional<SpotVisitLog> findFirstByMemberAndSpotOrderByVisitedAtDesc(Member member, Spot spot);
}
