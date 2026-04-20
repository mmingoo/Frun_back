package Termproject.Termproject2.domain.stats.dto.response;

import Termproject.Termproject2.domain.stats.dto.DayDistanceDto;
import Termproject.Termproject2.domain.stats.dto.StatsSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 주간 통계 응답
public class WeeklyStatsResponse {
    private StatsSummaryDto summary; // 주간 요약 통계
    private List<DayDistanceDto> chart; // 요일별 거리 차트 데이터
    private List<Object> friendStats; // 친구 통계 (미구현)
}
