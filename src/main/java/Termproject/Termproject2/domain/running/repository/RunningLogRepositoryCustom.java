package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.dto.FriendFeedResponseDto;

import java.util.List;

public interface RunningLogRepositoryCustom {
    List<FriendFeedResponseDto> findFriendFeeds(Long userId, Long cursorId, int size);
}
