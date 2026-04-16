package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.response.FeedScrollResponseDto;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedScrollResponseDto;
import Termproject.Termproject2.domain.running.repository.LikeRepository;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final RunningLogRepository runningLogRepository;
    private final ImageService imageService;
    private final LikeRepository likeRepository;

    // 친구 피드 커서 기반 조회
    @Override
    public FeedScrollResponseDto getFriendFeeds(Long userId, Long cursorId, int size) {

        // 커서 기반으로 친구 피드 조회
        List<FriendFeedResponseDto> result = runningLogRepository.findFriendFeeds(userId, cursorId, size);

        boolean hasNext = hasNext(result, size); // size + 1 조회 결과를 기반으로 다음 페이지 존재 여부(hasNext) 판단
        result = trimToSize(result, size); // 일부러 hasNext 판단하기 위해 size + 1 개수만큼 불러왔으므로 size 만큼 자름

        List<Long> logIds = extractLogIds(result); // 조회된 피드에서 러닝로그 ID 목록 추출

        // 러닝로그 ID 기준으로 이미지 목록을 한 번에 조회 (N+1 방지)
        Map<Long, List<String>> imagesMap = runningLogRepository.findImagesByRunningLogIds(logIds);

        result = attachFriendImages(result, imagesMap); // 각 피드 DTO에 이미지 URL 및 프로필 이미지 URL을 설정

        applyLikedStatus(result, userId, logIds); // 내가 좋아요 한 피드에 좋아요 표시


        // 다음 페이지가 존재하면 마지막 러닝로그 ID를 next cursor로 설정
        Long nextCursorId = getNextCursorId(result, hasNext, FriendFeedResponseDto::getRunningLogId);

        return new FeedScrollResponseDto(result, hasNext, nextCursorId);
    }



    // ===================== 유저 페이지 피드 =====================
    // 유저 페이지 피드 조회 (본인이면 비공개 포함, 타인이면 공개만)
    @Override
    public MyPageFeedScrollResponseDto getUserPageFeeds(Long viewerId, Long targetUserId, Long cursorId, int size) {
        // 조회하려는 사람이 작성자인지 확인
        boolean isOwner = viewerId.equals(targetUserId);

        // 마이페이지 피드 목록 조회
        List<MyPageFeedResponseDto> feeds = runningLogRepository.findUserPageFeeds(targetUserId, cursorId, size, isOwner);

        // 커서 기반 페이징
        boolean hasNext = hasNext(feeds, size);
        feeds = trimToSize(feeds, size);

        // 러닝일지의 id 들 (이미지 일괄 조회를 위함)
        List<Long> logIds = feeds.stream().map(MyPageFeedResponseDto::getRunningLogId).toList();

        Map<Long, String> imagesMap = runningLogRepository.findImageByRunningLogIds(logIds)
                .entrySet().stream() // key 와 value 를 쌍으로 처리하기 위함
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> imageService.getRunningLogImageUrl(e.getValue())
                ));

        List<MyPageFeedResponseDto> result = attachMyPageImages(feeds, imagesMap);

        Long nextCursorId = getNextCursorId(result, hasNext, MyPageFeedResponseDto::getRunningLogId);
        return new MyPageFeedScrollResponseDto(result, hasNext, nextCursorId);
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

    private void applyLikedStatus(List<FriendFeedResponseDto> result, Long userId, List<Long> logIds) {
        // 내가 좋아요 한 러닝일지 목록, 메인 피드 목록 중에 내가 좋아요 한 로그ID 들의 List
        Set<Long> likedLogIds = likeRepository.findLikedLogIds(userId, logIds);

        // 각 dto 를 순회하면서, 내가 좋아요한 로그ID List 중 dto의 로그ID 가 있다면 true 처리
        result.forEach(dto -> dto.setLiked(likedLogIds.contains(dto.getRunningLogId())));
    }
}

