package com.running.runapp.domain.spot.repository;

import com.running.runapp.domain.spot.domain.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpotRepository extends JpaRepository<Spot, Long>, SpotRepositoryCustom {
    boolean existsByName(String name);
    Optional<Spot> findByName(String name);

    @Query(value = "SELECT spot.spot_id, spot.name, spot.reward_amount, spot.latitude, spot.longitude, " +
            "(svl.spot_id IS NOT NULL) AS is_visited " + // 🚩 svl.id 대신 svl.spot_id로 체크 (더 안전함)
            "FROM spot AS spot " +
            "LEFT JOIN spot_visit_log AS svl ON svl.spot_id = spot.spot_id AND svl.member_id = :memberId " +
            "WHERE ST_DWithin(spot.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)\\:\\:geography, :distance) " +
            "ORDER BY ST_Distance(spot.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)\\:\\:geography) ASC",
            nativeQuery = true)
    List<Object[]> findNearbySpotsNative(
            @Param("memberId") Long memberId,
            @Param("lng") double lng,
            @Param("lat") double lat,
            @Param("distance") double distance
    );
}
