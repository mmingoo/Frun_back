package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.dto.request.FeedSortType;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedResponseDto;

import java.util.List;
import java.util.Map;

public interface RunningLogRepositoryCustom {
    //TODO: 친구 피드 커서 기반 조회 (신고 완료 게시물 제외)
    List<FriendFeedResponseDto> findFriendFeeds(Long userId, Long cursorId, int size);

    //TODO: 유저/마이페이지 피드 조회 (본인이면 비공개 포함, 정렬 지원)
    List<MyPageFeedResponseDto> findUserPageFeeds(Long userId, Long cursorId, String cursorValue, int size, boolean isOwner, FeedSortType sortType);

    //TODO: 여러 러닝로그의 전체 이미지 목록 일괄 조회
    Map<Long, List<String>> findImagesByRunningLogIds(List<Long> runningLogIds);

    //TODO: 여러 러닝로그의 대표 이미지(첫 번째) 1개씩 조회
    Map<Long, String> findImageByRunningLogIds(List<Long> logIds);
}
