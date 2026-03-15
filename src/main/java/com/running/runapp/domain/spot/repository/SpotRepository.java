package com.running.runapp.domain.spot.repository;

import com.running.runapp.domain.spot.domain.Spot;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpotRepository extends JpaRepository<Spot, Long>, SpotRepositoryCustom {
    boolean existsByName(String name);
    Optional<Spot> findByName(String name);

    @Query(value = "SELECT * FROM spot s " +
            "WHERE ST_DWithin(s.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, :distance) " +
            "ORDER BY ST_Distance(s.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography) ASC",
            nativeQuery = true)
    List<Spot> findNearbySpot(@Param("latitude") double latitude,
                              @Param("longitude") double longitude,
                              @Param("distance") double distance);
}
