package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedResponseDto;

import java.util.List;
import java.util.Map;

public interface RunningLogRepositoryCustom {
    List<FriendFeedResponseDto> findFriendFeeds(Long userId, Long cursorId, int size);
    List<MyPageFeedResponseDto> findUserPageFeeds(Long userId, Long cursorId, int size, boolean isOwner);
    Map<Long, List<String>> findImagesByRunningLogIds(List<Long> runningLogIds);
    Map<Long, String> findImageByRunningLogIds(List<Long> logIds);

}
