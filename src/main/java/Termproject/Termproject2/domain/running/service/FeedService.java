package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.request.FeedSortType;
import Termproject.Termproject2.domain.running.dto.response.FeedScrollResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedScrollResponseDto;

public interface FeedService {
    //TODO: 친구 피드 커서 기반 조회
    FeedScrollResponseDto getFriendFeeds(Long userId, Long cursorId, int size);

    //TODO: 유저 페이지 피드 조회 (본인이면 비공개 포함, 정렬 지원)
    MyPageFeedScrollResponseDto getUserPageFeeds(Long viewerId, Long targetUserId, Long cursorId, String cursorValue, int size, FeedSortType sortType);
}
