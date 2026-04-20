package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 통계 요약 (주간·월간·기간 공용)
public class StatsSummaryDto {
    private double totalDistanceKm; // 총 거리 (km)
    private int runCount; // 러닝 횟수
    private int avgPaceSec; // 평균 페이스 (초)
    private int totalDurationSec; // 총 러닝 시간 (초)




}
