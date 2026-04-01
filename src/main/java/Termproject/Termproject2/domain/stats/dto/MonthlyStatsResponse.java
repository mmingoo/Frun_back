package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MonthlyStatsResponse {
    private StatsSummaryDto summary;
    private List<MonthlyWeekDto> chart;
    private List<Object> friendStats;
}
