package com.running.runapp.global.common;

public class LevelCalculator {

    // 특정 레벨이 되기 위한 총 누적 경험치 계산식
    public static long getRequiredTotalExp(int level) {
        if (level <= 1) return 0;
        long n = level - 1;
        return 250 * n * n + 250 * n;
    }

    // 현재 총 경험치로 최종 레벨을 단번에 계산하는 역함수
    public static int calculateLevelFromExp(long totalExp) {
        if (totalExp <= 0) return 1;

        // 근의 공식을 코드화 (Math.sqrt 활용)
        double innerValue = 1 + (totalExp / 62.5);
        double n = (-1 + Math.sqrt(innerValue)) / 2.0;

        // 소수점 아래는 버리고(내림) + 1을 하여 현재 레벨 산출
        return (int) Math.floor(n) + 1;
    }
}