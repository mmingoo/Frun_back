package Termproject.Termproject2.domain.running.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 내 마이페이지 피드 무한 스크롤 응답
public class MyPageFeedScrollResponseDto {
    private List<MyPageFeedResponseDto> feeds; // 피드 목록
    private boolean hasNext; // 다음 페이지 존재 여부
    private Long nextCursorId; // 다음 커서 ID
}
