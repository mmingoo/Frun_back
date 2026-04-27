package Termproject.Termproject2.domain.running.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 내 마이페이지 피드 무한 스크롤 응답
public class MyPageFeedScrollResponseDto {
    private List<MyPageFeedResponseDto> feeds;
    private boolean hasNext;
    private Long nextCursorId;
    private String nextCursorValue; // CREATED_AT 정렬 시 null, 나머지 정렬 시 마지막 항목의 정렬 기준 값
}
