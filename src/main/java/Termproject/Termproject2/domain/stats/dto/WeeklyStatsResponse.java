package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class WeeklyStatsResponse {
    private StatsSummaryDto summary;
    private List<DayDistanceDto> chart;
    private List<Object> friendStats;
}
