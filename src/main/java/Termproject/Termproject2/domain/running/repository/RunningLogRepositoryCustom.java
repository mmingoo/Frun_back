package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.FriendPageFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedResponseDto;

import java.util.List;
import java.util.Map;

public interface RunningLogRepositoryCustom {
    List<FriendFeedResponseDto> findFriendFeeds(Long userId, Long cursorId, int size);
    List<MyPageFeedResponseDto> findMyFeeds(Long userId, Long cursorId, int size);
    List<FriendPageFeedResponseDto> findFriendPageFeeds(Long userId, Long cursorId, int size);
    Map<Long, List<String>> findImagesByRunningLogIds(List<Long> runningLogIds);
    Map<Long, String> findImageByRunningLogIds(List<Long> logIds);

}
