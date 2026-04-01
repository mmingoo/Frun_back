package Termproject.Termproject2.domain.stats.dto.response;

import Termproject.Termproject2.domain.stats.dto.PeriodDayDto;
import Termproject.Termproject2.domain.stats.dto.StatsSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PeriodStatsResponse {
    private StatsSummaryDto summary;
    private List<PeriodDayDto> chart;
    private List<Object> friendStats;
}
