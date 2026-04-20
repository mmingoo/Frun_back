package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 러닝 요약 통계 (주간/월간 공용)
public class RunSummaryDto {
    private double totalDistanceKm; // 총 거리 (km)
    private int totalCount; // 총 러닝 횟수
    private String avgPace; // 평균 페이스 (기록 없으면 null)
}
