package Termproject.Termproject2.domain.stats.dto.response;

import Termproject.Termproject2.domain.stats.dto.PeriodDayDto;
import Termproject.Termproject2.domain.stats.dto.StatsSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 기간별 통계 응답
public class PeriodStatsResponse {
    private StatsSummaryDto summary; // 기간 요약 통계
    private List<PeriodDayDto> chart; // 날짜별 거리 차트 데이터
    private List<Object> friendStats; // 친구 통계 (미구현)
}
