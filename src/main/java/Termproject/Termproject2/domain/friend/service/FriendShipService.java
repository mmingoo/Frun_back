package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.dto.request.FriendRequestDto;
import Termproject.Termproject2.domain.friend.dto.response.FriendListResponse;
import Termproject.Termproject2.domain.friend.dto.response.UserSearchListResponse;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;

public interface FriendShipService {
    FriendListResponse getFriendList(Long userId, String cursorName, Long cursorId, int size);

    void isFriendWithAuthor(Long userId, Long authorId);
    UserSearchListResponse searchUsersWithDetailStatus(Long currentUserId, String keyword, String cursorName, Long cursorId, int size);

    void unfriend(Long myId, Long friendId);

    void sendFriendRequest(Long userId, Long friendId);

    void acceptFriendRequest(Long senderId, Long userId);

    void rejectFriendRequest(Long senderId, Long userId);
    long getFriendCount(Long targetUserId);
    FriendRequestStatus getStatus(Long me, Long other);

    }
