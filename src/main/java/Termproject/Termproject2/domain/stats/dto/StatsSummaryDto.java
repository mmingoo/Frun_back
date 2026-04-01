package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatsSummaryDto {
    private double totalDistanceKm;
    private int runCount;
    private int avgPaceSec;
    private int totalDurationSec;




}
