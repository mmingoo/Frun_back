package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.request.FeedSortType;
import Termproject.Termproject2.domain.running.dto.response.FeedScrollResponseDto;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedScrollResponseDto;
import Termproject.Termproject2.domain.running.repository.LikeRepository;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedServiceImpl implements FeedService {

    private final RunningLogRepository runningLogRepository;
    private final ImageService imageService;
    private final LikeRepository likeRepository;

    //TODO: 친구 피드 커서 기반 조회
    @Override
    public FeedScrollResponseDto getFriendFeeds(Long userId, Long cursorId, int size) {

        // 커서 기반으로 친구 피드 조회
        List<FriendFeedResponseDto> result = runningLogRepository.findFriendFeeds(userId, cursorId, size);

        // size + 1 조회 결과를 기반으로 다음 페이지 존재 여부(hasNext) 판단
        boolean hasNext = hasNext(result, size);

        // 일부러 hasNext 판단하기 위해 size + 1 개수만큼 불러왔으므로 size 만큼 자름
        result = trimToSize(result, size);

        // 조회된 피드에서 러닝로그 ID 목록 추출
        List<Long> logIds = extractLogIds(result);

        // 러닝로그 ID 기준으로 이미지 목록을 한 번에 조회 (N+1 방지)
        Map<Long, List<String>> imagesMap = runningLogRepository.findImagesByRunningLogIds(logIds);

        // 각 피드 DTO에 이미지 URL 및 프로필 이미지 URL을 설정
        result = attachFriendImages(result, imagesMap);

        // 내가 좋아요 한 피드에 좋아요 표시
        applyLikedStatus(result, userId, logIds);

        // 다음 페이지가 존재하면 마지막 러닝로그 ID를 next cursor로 설정
        Long nextCursorId = getNextCursorId(result, hasNext, FriendFeedResponseDto::getRunningLogId);

        return new FeedScrollResponseDto(result, hasNext, nextCursorId);
    }



    //TODO: 유저 페이지 피드 커서 기반 조회 (본인이면 비공개 포함, 정렬 지원)
    @Override
    public MyPageFeedScrollResponseDto getUserPageFeeds(Long viewerId, Long targetUserId, Long cursorId, String cursorValue, int size, FeedSortType sortType) {
        // 본인의 마이페이지인지
        boolean isOwner = viewerId.equals(targetUserId);

        // 피드 조회
        List<MyPageFeedResponseDto> feeds = runningLogRepository.findUserPageFeeds(targetUserId, cursorId, cursorValue, size, isOwner, sortType);

        // 커서로 불러올 다음 페이지가 있는지 여부
        boolean hasNext = hasNext(feeds, size);

        // 데이터를 size + 1 개 만큼 조회했으므로 size 만큼 데이터를 자름
        feeds = trimToSize(feeds, size);

        // logId 목록들 추출
        List<Long> logIds = feeds.stream().map(MyPageFeedResponseDto::getRunningLogId).toList();

        // 썸네일 이미지 목록들을 key : value 형식으로 조회
        Map<Long, String> imagesMap = runningLogRepository.findImageByRunningLogIds(logIds)
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> imageService.getRunningLogImageUrl(e.getValue())
                ));

        // 각 게시물에 썸네일 이미지 부착
        attachMyPageImages(feeds, imagesMap);

        // 다음 cursorId 구하기
        Long nextCursorId = getNextCursorId(feeds, hasNext, MyPageFeedResponseDto::getRunningLogId);

        // 다음 페이지의 CurosrValue 구하기
        String nextCursorValue = extractNextCursorValue(feeds, hasNext, sortType);

        return new MyPageFeedScrollResponseDto(feeds, hasNext, nextCursorId, nextCursorValue);
    }





    // ===================== 공통 로직 =====================

    // 다음 페이지 존재 여부 판단
    private boolean hasNext(List<?> list, int size) {
        return list.size() > size;
    }

    // size 기준으로 리스트 자르기
    private <T> List<T> trimToSize(List<T> list, int size) {
        return list.size() > size ? list.subList(0, size) : list;
    }

    // next cursor 계산 (제네릭)
    private <T> Long getNextCursorId(List<T> result, boolean hasNext, Function<T, Long> idExtractor) {
        if (!hasNext || result.isEmpty()) {
            return null;
        }
        return idExtractor.apply(result.get(result.size() - 1));
    }

    // ===================== Friend 전용 =====================

    // 조회된 피드에서 러닝로그 ID 목록 추출
    private List<Long> extractLogIds(List<FriendFeedResponseDto> feeds) {
        return feeds.stream()
                .map(FriendFeedResponseDto::getRunningLogId)
                .collect(Collectors.toList());
    }

    // 각 피드 DTO에 이미지 URL 및 프로필 이미지 URL을 세팅
    // - 러닝로그 이미지는 URL 변환 후 리스트로 매핑
    // - 이미지가 없으면 빈 리스트 반환
    private List<FriendFeedResponseDto> attachFriendImages(
            List<FriendFeedResponseDto> feeds,
            Map<Long, List<String>> imagesMap
    ) {
        return feeds.stream()
                // 러닝로그 이미지는 URL 변환 후 리스트로 매핑
                .map(dto -> {
                    List<String> images = imagesMap.getOrDefault(dto.getRunningLogId(), List.of())
                            .stream()
                            .map(imageService::getRunningLogImageUrl)
                            .toList();

                    return new FriendFeedResponseDto(
                            dto.getRunningLogId(),
                            dto.getUserId(),
                            dto.getNickName(),
                            imageService.getProfileImageUrl(dto.getImageUrl()), // 프로필 이미지 URL 변환
                            dto.getRunDate(),
                            dto.getRunTime(),
                            dto.getDistance(),
                            dto.getPace(),
                            dto.getDuration(),
                            dto.getMemo(),
                            dto.getCreatedAt(),
                            dto.getCommentCtn(),
                            dto.getLikeCtn(),
                            dto.isLiked(),
                            images,
                            dto.isPublic()
                    );
                })
                .toList();
    }

    // ===================== MyPage 전용 =====================

    // 피드 목록에 썸네일 이미지를 직접 세팅 (in-place)
    private void attachMyPageImages(List<MyPageFeedResponseDto> feeds, Map<Long, String> imagesMap) {
        feeds.forEach(dto -> dto.setThumbnailImage(imagesMap.get(dto.getRunningLogId())));
    }

    // 다음 페이지의 cursorValue 추출 (CREATED_AT은 ID만으로 충분하므로 null)
    private String extractNextCursorValue(List<MyPageFeedResponseDto> result, boolean hasNext, FeedSortType sortType) {
        if (!hasNext || result.isEmpty() || sortType == FeedSortType.CREATED_AT) return null;
        MyPageFeedResponseDto last = result.get(result.size() - 1);
        return switch (sortType) {
            case RUN_DATE  -> last.getRunDate().toString();
            case RUN_TIME  -> last.getRunTime() != null ? last.getRunTime().toString() : null;
            case DISTANCE  -> last.getDistance().toPlainString();
            case PACE      -> last.getPaceSeconds() != null ? String.valueOf(last.getPaceSeconds()) : null;
            default        -> null;
        };
    }

    private void applyLikedStatus(List<FriendFeedResponseDto> result, Long userId, List<Long> logIds) {

        // 내가 좋아요 한 runningLogId 목록, 메인 피드 목록 중에 내가 좋아요 한 로그ID 들의 List
        Set<Long> likedLogIds = likeRepository.findLikedLogIds(userId, logIds);

        // 각 dto 를 순회하면서, 내가 좋아요한 로그ID List 중 dto의 로그 ID가 있다면 true 처리
        result.forEach(dto -> dto.setLiked(likedLogIds.contains(dto.getRunningLogId())));
    }
}

