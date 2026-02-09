package com.running.runapp.domain.running.util;

import com.running.runapp.domain.running.dto.LatLng;
import com.running.runapp.domain.running.dto.RunRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DistanceUtilsTest {

    @Test
    void path가_null이면_0() {
        RunRequest.RunFinishRequest dto = new RunRequest.RunFinishRequest(LocalDateTime.now(), null);

        double dist = DistanceUtils.totalDistanceMeter(dto);
        assertEquals(0.0, dist, 0.0001);
    }

    @Test
    void path가_1개면_0() {
        List<LatLng> path = List.of(new LatLng(0.0, 0.0));
        RunRequest.RunFinishRequest dto = new RunRequest.RunFinishRequest(LocalDateTime.now(), path);

        double dist = DistanceUtils.totalDistanceMeter(dto);
        assertEquals(0.0, dist, 0.0001);
    }

    @Test
    void 두점_위도1도차이_약111km() {
        List<LatLng> path = List.of(
                new LatLng(0.0, 0.0),
                new LatLng(1.0, 0.0)
        );
        RunRequest.RunFinishRequest dto = new RunRequest.RunFinishRequest(LocalDateTime.now(), path);

        double dist = DistanceUtils.totalDistanceMeter(dto);
        assertTrue(dist > 110_000 && dist < 112_500, "dist=" + dist);
    }

    @Test
    void 세점_누적거리_검증_약222km() {
        List<LatLng> path = List.of(
                new LatLng(0.0, 0.0),
                new LatLng(1.0, 0.0),
                new LatLng(1.0, 1.0)
        );
        RunRequest.RunFinishRequest dto = new RunRequest.RunFinishRequest(LocalDateTime.now(), path);

        double dist = DistanceUtils.totalDistanceMeter(dto);

        System.out.println("dist=" + dist);
        assertTrue(dist > 218_000 && dist < 225_000, "dist=" + dist);
    }

    @Test
    void 곡선처럼_여러점_찍으면_직선보다_길어짐() {
        List<LatLng> straight = List.of(
                new LatLng(0.0, 0.0),
                new LatLng(0.0, 1.0)
        );

        List<LatLng> curved = List.of(
                new LatLng(0.0, 0.0),
                new LatLng(0.2, 0.25),
                new LatLng(0.2, 0.75),
                new LatLng(0.0, 1.0)
        );

        double straightDist = DistanceUtils.totalDistanceMeter(
                new RunRequest.RunFinishRequest(LocalDateTime.now(), straight)
        );

        double curvedDist = DistanceUtils.totalDistanceMeter(
                new RunRequest.RunFinishRequest(LocalDateTime.now(), curved)
        );

        System.out.println("straight=" + straightDist + ", curved=" + curvedDist);

        assertTrue(curvedDist > straightDist, "curvedDist should be greater than straightDist");
    }
}