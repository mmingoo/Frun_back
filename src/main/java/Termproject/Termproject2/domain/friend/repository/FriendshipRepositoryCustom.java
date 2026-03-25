package Termproject.Termproject2.domain.friend.repository;

import Termproject.Termproject2.domain.friend.dto.FriendResponseDto;

import java.util.List;

public interface FriendshipRepositoryCustom {
    List<FriendResponseDto> getFriendList(Long userId, String cursorName, Long cursorId, int size);
}
