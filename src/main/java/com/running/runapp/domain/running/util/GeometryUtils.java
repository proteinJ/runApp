package com.running.runapp.domain.running.util;

import com.running.runapp.domain.running.dto.LatLng;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;

public final class GeometryUtils {

    private GeometryUtils() { }
    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    public static LineString toLineString(List<LatLng> path) {
        if (path == null || path.size() < 2) {
            throw new IllegalArgumentException("path는 최소 2개 좌표가 필요합니다.");
        }

        Coordinate[] coords = path.stream()
                .map(p -> new Coordinate(p.lng(), p.lat())) // x=lng, y=lat
                .toArray(Coordinate[]::new);

        LineString lineString = GEOMETRY_FACTORY.createLineString(coords);
        lineString.setSRID(4326);
        return lineString;
    }

    public static List<LatLng> toLatLngList(LineString lineString) {
        List<LatLng> result = new ArrayList<>();
        if (lineString == null) return result;

        for (Coordinate c : lineString.getCoordinates()) {
            result.add(new LatLng(c.getY(), c.getX())); // lat=y, lng=x
        }
        return result;
    }
}
