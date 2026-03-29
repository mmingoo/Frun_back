package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;

import java.util.List;
import java.util.Map;

public interface RunningLogRepositoryCustom {
    List<FriendFeedResponseDto> findFriendFeeds(Long userId, Long cursorId, int size);
    Map<Long, List<String>> findImagesByRunningLogIds(List<Long> runningLogIds);
}
