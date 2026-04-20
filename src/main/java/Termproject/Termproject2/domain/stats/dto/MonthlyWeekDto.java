package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 월별 통계 내 주차별 데이터
public class MonthlyWeekDto {
    private String weekLabel; // 주차 레이블 ("1주", "2주" 등)
    private double totalDistanceKm; // 해당 주 총 거리 (km)
    private List<DayDistanceDto> days; // 요일별 거리 목록
}
