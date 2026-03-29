package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.response.FeedScrollResponseDto;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
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

    public FeedScrollResponseDto getFriendFeeds(Long userId, Long cursorId, int size) {
        List<FriendFeedResponseDto> result = runningLogRepository.findFriendFeeds(userId, cursorId, size);

        boolean hasNext = result.size() > size;
        if (hasNext) {
            result = result.subList(0, size);
        }

        // 조회된 로그 ID 목록으로 이미지 한 번에 조회
        List<Long> logIds = result.stream()
                .map(FriendFeedResponseDto::getRunningLogId)
                .collect(Collectors.toList());
        Map<Long, List<String>> imagesMap = runningLogRepository.findImagesByRunningLogIds(logIds);

        result = result.stream()
                .map(dto -> {
                    List<String> images = imagesMap.getOrDefault(dto.getRunningLogId(), List.of())
                            .stream()
                            .map(imageService::getRunningLogImageUrl)
                            .collect(Collectors.toList());
                    return new FriendFeedResponseDto(
                            dto.getRunningLogId(), dto.getUserId(), dto.getNickName(),
                            imageService.getProfileImageUrl(dto.getImageUrl()),
                            dto.getRunDate(), dto.getDistance(), dto.getPace(), dto.getDuration(),
                            dto.getMemo(), dto.getCreatedAt(), dto.getCommentCtn(), dto.getLikeCtn(),
                            images
                    );
                })
                .collect(Collectors.toList());

        Long nextCursorId = hasNext ? result.get(result.size() - 1).getRunningLogId() : null;

        return new FeedScrollResponseDto(result, hasNext, nextCursorId);
    }
}
