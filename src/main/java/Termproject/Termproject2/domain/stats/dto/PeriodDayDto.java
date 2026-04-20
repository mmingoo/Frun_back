package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 기간별 통계 내 날짜별 데이터
public class PeriodDayDto {
    private String date; // 날짜 ("2026-03-01" 형식)
    private String dayOfWeek; // 요일 ("MON"~"SUN")
    private double distanceKm; // 해당 날 거리 (km)
}
