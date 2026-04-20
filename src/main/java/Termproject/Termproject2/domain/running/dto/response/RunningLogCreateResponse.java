package Termproject.Termproject2.domain.running.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 러닝일지 생성 응답
public class RunningLogCreateResponse {
    private Long logId; // 생성된 러닝일지 ID
}