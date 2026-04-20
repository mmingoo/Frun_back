package Termproject.Termproject2.domain.stats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
// 통계 요약 응답 DTO (주간·월간 통합)
public class StatsSummaryResponseDto {
    double weeklyRunDistance; // 주간 총 거리 (km)
    int weeklyRunCnt; // 주간 러닝 횟수
    int weeklyPaceAvg; // 주간 평균 페이스 (초)
    int weeklyTotalDurationSec; // 주간 총 러닝 시간 (초)
    double monthlyRunDistance; // 월간 총 거리 (km)
    int monthlyRunCnt; // 월간 러닝 횟수
    int monthlyPaceAvg; // 월간 평균 페이스 (초)
    int monthlyTotalDurationSec; // 월간 총 러닝 시간 (초)

}
