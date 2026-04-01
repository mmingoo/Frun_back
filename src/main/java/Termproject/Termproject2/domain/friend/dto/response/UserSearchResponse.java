package Termproject.Termproject2.domain.friend.dto.response;

import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserSearchResponse {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private FriendRequestStatus friendStatus;
}