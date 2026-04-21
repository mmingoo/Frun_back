package Termproject.Termproject2.domain.friend.dto.response;

import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
// 유저 검색 결과 단건 응답
public class UserSearchResponse {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private FriendRequestStatus friendStatus; // 친구 관계 상태
}