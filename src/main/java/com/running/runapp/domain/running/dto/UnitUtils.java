// UnitUtils.java
package com.running.runapp.domain.running.dto;

public class UnitUtils {
    private UnitUtils() {}

    // meters -> km (소수 2자리)
    public static Double metersToKm(Double meters) {
        if (meters == null) return 0.0;
        double km = meters / 1000.0;
        return Math.round(km * 100.0) / 100.0;
    }

    // seconds/km -> "mm:ss"
    public static String secondsToPaceText(Integer secPerKm) {
        if (secPerKm == null || secPerKm <= 0) return null;

        int mm = secPerKm / 60;
        int ss = secPerKm % 60;

        // mm은 보통 0 패딩 안 해도 되는데, 원하면 %02d로 바꿔도 됨
        return String.format("%d:%02d", mm, ss);
    }
}