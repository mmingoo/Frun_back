package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DayDistanceDto {
    private String dayOfWeek;   // "MON", "TUE" 등
    private double distanceKm;
}
