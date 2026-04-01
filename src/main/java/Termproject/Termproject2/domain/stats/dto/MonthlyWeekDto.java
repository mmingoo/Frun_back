package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MonthlyWeekDto {
    private String weekLabel;           // "1주", "2주", ...
    private double totalDistanceKm;
    private List<DayDistanceDto> days;
}
