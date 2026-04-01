package Termproject.Termproject2.domain.stats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class StatsSummaryResponseDto {
    double weeklyRunDistance;
    int weeklyRunCnt;
    int weeklyPaceAvg;
    int weeklyTotalDurationSec;
    double monthlyRunDistance;
    int monthlyRunCnt;
    int monthlyPaceAvg;
    int monthlyTotalDurationSec;

}
