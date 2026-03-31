package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.response.FeedScrollResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedScrollResponseDto;

public interface FeedService {
    FeedScrollResponseDto getFriendFeeds(Long userId, Long cursorId, int size);
    MyPageFeedScrollResponseDto getUserPageFeeds(Long viewerId, Long targetUserId, Long cursorId, int size);
}
