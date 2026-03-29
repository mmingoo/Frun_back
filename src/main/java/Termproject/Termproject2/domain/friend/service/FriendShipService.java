package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.dto.FriendListResponse;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;

public interface FriendShipService {
    FriendListResponse getFriendList(Long userId, String cursorName, Long cursorId, int size);

    void isFriendWithAuthor(Long userId, Long authorId);

}
