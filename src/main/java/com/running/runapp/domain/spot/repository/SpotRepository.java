package com.running.runapp.domain.spot.repository;

import com.running.runapp.domain.spot.domain.Spot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    Page<Spot> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByName(String name);

    @Query(value = "SELECT s.spot_id, s.name, s.reward_amount, s.latitude, s.longitude, " +
            "NOT EXISTS (SELECT 1 FROM spot_visit_log v WHERE v.spot_id = s.spot_id AND v.member_id = :memberId " +
            "AND v.visited_at > NOW() AT TIME ZONE 'Asia/Seoul' - INTERVAL '24 hours') as can_check_in " +
            "FROM spot s " +
            "WHERE ST_DWithin(s.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, :distance) " +
            "ORDER BY ST_Distance(s.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography) ASC",
            nativeQuery = true)
    List<Object[]> findNearbySpotsNative(@Param("memberId") Long memberId,
                                         @Param("longitude") double longitude,
                                         @Param("latitude") double latitude,
                                         @Param("distance") double distance);
}