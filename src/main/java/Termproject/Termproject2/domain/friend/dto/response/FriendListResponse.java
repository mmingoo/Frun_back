package Termproject.Termproject2.domain.friend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 친구 목록 커서 기반 응답
public class FriendListResponse {
    private List<FriendResponseDto> friends; // 친구 목록
    private boolean hasNext; // 다음 페이지 존재 여부
    private String nextCursorName; // 다음 커서 닉네임
}
