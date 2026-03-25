package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.FeedScrollResponseDto;

public interface FeedService {
    FeedScrollResponseDto getFriendFeeds(Long userId, Long cursorId, int size);
}
