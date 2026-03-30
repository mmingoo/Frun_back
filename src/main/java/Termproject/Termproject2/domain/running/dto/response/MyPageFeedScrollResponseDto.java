package Termproject.Termproject2.domain.running.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyPageFeedScrollResponseDto {
    private List<MyPageFeedResponseDto> feeds;
    private boolean hasNext;
    private Long nextCursorId;
}
