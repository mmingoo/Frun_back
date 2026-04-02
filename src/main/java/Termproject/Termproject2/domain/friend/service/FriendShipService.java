package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.dto.response.FriendListResponse;
import Termproject.Termproject2.domain.friend.dto.response.UserSearchListResponse;

public interface FriendShipService {
    FriendListResponse getFriendList(Long userId, String cursorName, Long cursorId, int size);

    void isFriendWithAuthor(Long userId, Long authorId);
    UserSearchListResponse searchUsersWithDetailStatus(Long currentUserId, String keyword, String cursorName, Long cursorId, int size);
}
