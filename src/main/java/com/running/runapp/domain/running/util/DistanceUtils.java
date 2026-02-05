package com.running.runapp.domain.running.util;

import com.running.runapp.domain.running.dto.LatLng;
import com.running.runapp.domain.running.dto.RunRequest;

import java.util.List;

import static com.running.runapp.global.common.LocationUtils.calculateDistance;

public class DistanceUtils {

    // path 전체 누적 거리(m)
    public static double totalDistanceMeter(RunRequest.RunFinishRequest dto) {
        List<LatLng> path = dto.getPath();

        if (path == null || path.size() < 2) return 0.0;

        double totalSum = 0.0;
        for (int i = 1; i < path.size(); i++) {
            LatLng p1 = path.get(i - 1);
            LatLng p2 = path.get(i);
            totalSum += calculateDistance(p1.lat(), p1.lng(), p2.lat(), p2.lng());
        }
        return totalSum;
    }
}