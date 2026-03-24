package com.running.runapp.domain.spot.repository;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.spot.dto.SpotResponse;

import java.util.List;

public interface SpotRepositoryCustom {
    List<SpotResponse.SummaryInfo> findNearbyWithCheckInStatus(Long memberId, Double lat, Double lon, Double distance);
}
