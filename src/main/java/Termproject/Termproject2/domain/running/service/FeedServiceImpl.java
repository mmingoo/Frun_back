package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.response.FeedScrollResponseDto;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedScrollResponseDto;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final RunningLogRepository runningLogRepository;
    private final ImageService imageService;

    // 친구 피드 커서 기반 조회
    public FeedScrollResponseDto getFriendFeeds(Long userId, Long cursorId, int size) {

        // 커서 기반으로 친구 피드 조회
        List<FriendFeedResponseDto> result = runningLogRepository.findFriendFeeds(userId, cursorId, size);

        // size + 1 조회 결과를 기반으로 다음 페이지 존재 여부(hasNext) 판단
        boolean hasNext = result.size() > size;
        if (hasNext) {
            result = result.subList(0, size);
        }

        // 조회된 피드에서 러닝로그 ID 목록 추출
        List<Long> logIds = result.stream()
                .map(FriendFeedResponseDto::getRunningLogId)
                .collect(Collectors.toList());

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
        result = result.stream()
                .map(dto -> {
                    List<String> images = imagesMap.getOrDefault(dto.getRunningLogId(), List.of())
                            .stream()
                            .map(imageService::getRunningLogImageUrl)
                            .collect(Collectors.toList());

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
                .collect(Collectors.toList());

        // 다음 페이지가 존재하면 마지막 러닝로그 ID를 next cursor로 설정
        Long nextCursorId = hasNext ? result.get(result.size() - 1).getRunningLogId() : null;

        return new FeedScrollResponseDto(result, hasNext, nextCursorId);
    }

    // 마이페이지 피드 조회
    @Override
    public MyPageFeedScrollResponseDto getMyPageFeeds(Long userId, Long cursorId, int size) {

        // 마이페이지에 있는 피드 조회
        List<MyPageFeedResponseDto> result = runningLogRepository.findMyFeeds(userId, cursorId, size);

        // 다음 페이지 존재 여부 판단
        boolean hasNext = result.size() > size;
        if (hasNext) {
            result = result.subList(0, size);
        }

        // 조회된 피드에서 러닝로그 id 추출
        List<Long> logIds = result.stream()
                .map(MyPageFeedResponseDto::getRunningLogId)
                .collect(Collectors.toList());

        // 러닝로그 Id 기준으로 이미지 목록 조회
        Map<Long, List<String>> imagesMap = runningLogRepository.findImagesByRunningLogIds(logIds);

        // 러닝로그별 이미지 URL 변환 후 DTO에 세팅 (이미지 없으면 빈 리스트)
        result = result.stream()
                .map(dto -> {
                    List<String> images = imagesMap.getOrDefault(dto.getRunningLogId(), List.of())
                            .stream()
                            .map(imageService::getRunningLogImageUrl)
                            .collect(Collectors.toList());

                    return new MyPageFeedResponseDto(
                            dto.getAuthorId(),dto.getRunningLogId(), dto.getRunDate(), dto.getDistance(),
                            dto.getPace(), dto.getDuration(), dto.getLikeCtn(), images
                    );
                })
                .collect(Collectors.toList());

        // 다음 페이지가 존재하면 마지막 러닝로그 ID를 next cursor로 설정
        Long nextCursorId = hasNext ? result.get(result.size() - 1).getRunningLogId() : null;

        return new MyPageFeedScrollResponseDto(result, hasNext, nextCursorId);
    }
}
