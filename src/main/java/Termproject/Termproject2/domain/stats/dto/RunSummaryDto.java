package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RunSummaryDto {
    private double totalDistanceKm;
    private int totalCount;
    private String avgPace; // null if no records
}
