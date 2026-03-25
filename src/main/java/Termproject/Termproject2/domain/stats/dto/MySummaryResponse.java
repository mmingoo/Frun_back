package Termproject.Termproject2.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MySummaryResponse {
    private RunSummaryDto weekly;
    private RunSummaryDto monthly;
}
