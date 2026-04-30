package Termproject.Termproject2.domain.friend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 유저 검색 목록 커서 기반 응답
public class UserSearchListResponse {
    private List<UserSearchResponse> users; // 검색된 유저 목록
    private boolean hasNext; // 다음 페이지 존재 여부
    private String nextCursorName;
}
