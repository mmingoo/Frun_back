package Termproject.Termproject2.domain.stats.dto.response;

import Termproject.Termproject2.domain.stats.dto.MonthlyWeekDto;
import Termproject.Termproject2.domain.stats.dto.StatsSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 월별 통계 응답
public class MonthlyStatsResponse {
    private StatsSummaryDto summary; // 월 요약 통계
    private List<MonthlyWeekDto> chart; // 주차별 거리 차트 데이터
    private List<Object> friendStats; // 친구 통계 (미구현)
}
