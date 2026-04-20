package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 요일별 거리 데이터
public class DayDistanceDto {
    private String dayOfWeek; // 요일 ("MON", "TUE" 등)
    private double distanceKm; // 해당 요일 거리 (km)
}
