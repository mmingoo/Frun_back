package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.response.FeedScrollResponseDto;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.FriendPageFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.FriendPageFeedScrollResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedScrollResponseDto;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final RunningLogRepository runningLogRepository;
    private final ImageService imageService;

    // ===================== 친구 피드 =====================

    // 친구 피드 커서 기반 조회
    @Override
    public FeedScrollResponseDto getFriendFeeds(Long userId, Long cursorId, int size) {

        // 커서 기반으로 친구 피드 조회
        List<FriendFeedResponseDto> result = runningLogRepository.findFriendFeeds(userId, cursorId, size);

        // size + 1 조회 결과를 기반으로 다음 페이지 존재 여부(hasNext) 판단
        boolean hasNext = hasNext(result, size);
        result = trimToSize(result, size);

        // 조회된 피드에서 러닝로그 ID 목록 추출
        List<Long> logIds = extractLogIds(result);

        // 러닝로그 ID 기준으로 이미지 목록을 한 번에 조회 (N+1 방지)
        /**
         * 예시:
         * {
         *   1L: ["a.jpg", "b.jpg"],
         *   2L: ["c.jpg"]
         * }
         */
        Map<Long, List<String>> imagesMap = runningLogRepository.findImagesByRunningLogIds(logIds);

        // 각 피드 DTO에 이미지 URL 및 프로필 이미지 URL을 세팅
        // - 러닝로그 이미지는 URL 변환 후 리스트로 매핑
        // - 이미지가 없으면 빈 리스트 반환
        result = attachFriendImages(result, imagesMap);

        // 다음 페이지가 존재하면 마지막 러닝로그 ID를 next cursor로 설정
        Long nextCursorId = getNextCursorId(result, hasNext, FriendFeedResponseDto::getRunningLogId);

        return new FeedScrollResponseDto(result, hasNext, nextCursorId);
    }

    // ===================== 마이페이지 피드 =====================

    // 마이페이지 피드 조회
    @Override
    public MyPageFeedScrollResponseDto getMyPageFeeds(Long userId, Long cursorId, int size) {

        // 마이페이지에 있는 피드 조회
        List<MyPageFeedResponseDto> feeds = runningLogRepository.findMyFeeds(userId, cursorId, size);

        // 다음 페이지 존재 여부 판단
        boolean hasNext = hasNext(feeds, size);
        feeds = trimToSize(feeds, size);

        // 러닝로그 Id 기준으로 썸네일 이미지 1장씩 조회
        List<Long> logIds = feeds.stream().map(MyPageFeedResponseDto::getRunningLogId).toList();

        Map<Long, String> imagesMap = runningLogRepository.findImageByRunningLogIds(logIds)
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> imageService.getRunningLogImageUrl(e.getValue())
                ));

        List<MyPageFeedResponseDto> result = attachMyPageImages(feeds, imagesMap);

        // 다음 페이지가 존재하면 마지막 러닝로그 ID를 next cursor로 설정
        Long nextCursorId = getNextCursorId(result, hasNext, MyPageFeedResponseDto::getRunningLogId);

        return new MyPageFeedScrollResponseDto(result, hasNext, nextCursorId);
    }

    // ===================== 친구 페이지 피드 =====================
    @Override
    public FriendPageFeedScrollResponseDto getFriendPageFeeds(Long friendId, Long cursorId, int size) {
        List<FriendPageFeedResponseDto> feeds = runningLogRepository.findFriendPageFeeds(friendId, cursorId, size);

        boolean hasNext = hasNext(feeds, size);
        feeds = trimToSize(feeds, size);

        List<Long> logIds = feeds.stream()
                .map(FriendPageFeedResponseDto::getRunningLogId)
                .toList();

        Map<Long, String> imagesMap = runningLogRepository.findImageByRunningLogIds(logIds)
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> imageService.getRunningLogImageUrl(e.getValue())
                ));

        List<FriendPageFeedResponseDto> result = feeds.stream()
                .map(dto -> {
                    String thumbnailUrl = imagesMap.get(dto.getRunningLogId());
                    return new FriendPageFeedResponseDto(
                            dto.getAuthorId(),
                            dto.getRunningLogId(),
                            dto.getRunDate(),
                            dto.getDistance(),
                            dto.getPace(),
                            dto.getDuration(),
                            dto.getLikeCtn(),
                            dto.getCommentCtn(),
                            dto.getMemo(),
                            thumbnailUrl
                    );
                })
                .toList();

        Long nextCursorId = getNextCursorId(result, hasNext, FriendPageFeedResponseDto::getRunningLogId);
        return new FriendPageFeedScrollResponseDto(result, hasNext, nextCursorId);
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
                            dto.getDistance(),
                            dto.getPace(),
                            dto.getDuration(),
                            dto.getMemo(),
                            dto.getCreatedAt(),
                            dto.getCommentCtn(),
                            dto.getLikeCtn(),
                            images
                    );
                })
                .toList();
    }

    // ===================== MyPage 전용 =====================

    private List<MyPageFeedResponseDto> attachMyPageImages(
            List<MyPageFeedResponseDto> feeds,
            Map<Long, String> imagesMap
    ) {
        return feeds.stream()
                .map(dto -> {
                    String thumbnail = imagesMap.get(dto.getRunningLogId());

                    return new MyPageFeedResponseDto(
                            dto.getAuthorId(),
                            dto.getRunningLogId(),
                            dto.getRunDate(),
                            dto.getDistance(),
                            dto.getPace(),
                            dto.getDuration(),
                            dto.getLikeCtn(),
                            dto.getCommentCtn(),
                            dto.getMemo(),
                            thumbnail
                    );
                })
                .toList();
    }
}