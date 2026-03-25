package Termproject.Termproject2.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FriendListResponse {
    private List<FriendResponseDto> friends;
    private boolean hasNext;
    private Long nextCursorId;
    private String nextCursorName;
}
