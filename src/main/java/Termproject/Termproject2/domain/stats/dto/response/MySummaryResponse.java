package Termproject.Termproject2.domain.stats.dto.response;

import Termproject.Termproject2.domain.stats.dto.RunSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 내 러닝 요약 (주간·월간) 응답
public class MySummaryResponse {
    private RunSummaryDto weekly; // 주간 요약
    private RunSummaryDto monthly; // 월간 요약
}
