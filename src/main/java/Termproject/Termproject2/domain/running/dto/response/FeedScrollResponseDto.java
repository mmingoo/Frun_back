package Termproject.Termproject2.domain.running.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FeedScrollResponseDto {
    private List<FriendFeedResponseDto> feeds;
    private boolean hasNext;
    private Long nextCursorId;
}
