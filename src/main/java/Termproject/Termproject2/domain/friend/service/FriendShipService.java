package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.dto.response.FriendListResponse;
import Termproject.Termproject2.domain.friend.dto.response.UserSearchResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendShipService {
    FriendListResponse getFriendList(Long userId, String cursorName, Long cursorId, int size);

    void isFriendWithAuthor(Long userId, Long authorId);
    List<UserSearchResponse> searchUsersWithDetailStatus(Long currentUserId, String keyword, Pageable pageable);


    }
