package com.running.runapp.domain.point;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.spot.domain.Spot;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface PointHistoryRepository extends CrudRepository<PointHistory, Long> {

    Optional<PointHistory> findTopByMemberAndSpotAndTypeOrderByCreatedAtDesc(
            Member member, Spot spot, PointHistory.PointType type
    );
}
