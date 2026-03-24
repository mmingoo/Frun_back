package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.FriendFeedResponseDto;

import java.util.List;

public interface FeedService {
        List<FriendFeedResponseDto> getFriendFeeds(Long userId, int page, int size);
    }
