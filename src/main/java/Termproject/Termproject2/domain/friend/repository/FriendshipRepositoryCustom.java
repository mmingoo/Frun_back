package Termproject.Termproject2.domain.friend.repository;

import Termproject.Termproject2.domain.friend.dto.FriendResponseDto;
import Termproject.Termproject2.domain.friend.entity.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepositoryCustom {
    List<FriendResponseDto> getFriendList(Long userId, String cursorName, Long cursorId, int size);
    Optional<Friendship> findByUserIdAndAuthorId(Long userId, Long authorId);

}
