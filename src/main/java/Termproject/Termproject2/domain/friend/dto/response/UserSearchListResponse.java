package Termproject.Termproject2.domain.friend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserSearchListResponse {
    private List<UserSearchResponse> users;
    private boolean hasNext;
    private Long nextCursorId;
    private String nextCursorName;
}
