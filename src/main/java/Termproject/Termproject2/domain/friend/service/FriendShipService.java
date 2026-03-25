package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.dto.FriendListResponse;

public interface FriendShipService {
    FriendListResponse getFriendList(Long userId, String cursorName, Long cursorId, int size);
}
