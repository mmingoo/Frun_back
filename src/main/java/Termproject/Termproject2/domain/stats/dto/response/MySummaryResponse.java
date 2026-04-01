package Termproject.Termproject2.domain.stats.dto.response;

import Termproject.Termproject2.domain.stats.dto.RunSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MySummaryResponse {
    private RunSummaryDto weekly;
    private RunSummaryDto monthly;
}
