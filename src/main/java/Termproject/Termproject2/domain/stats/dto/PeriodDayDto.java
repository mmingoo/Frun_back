package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PeriodDayDto {
    private String date;        // "2026-03-01"
    private String dayOfWeek;   // "SAT"
    private double distanceKm;
}
