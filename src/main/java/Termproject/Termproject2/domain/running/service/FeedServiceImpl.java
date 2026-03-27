package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.response.FeedScrollResponseDto;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

        result = result.stream()
                .map(dto -> new FriendFeedResponseDto(
                        dto.getRunningLogId(), dto.getUserId(), dto.getNickName(),
                        imageService.getImageUrl(dto.getImageUrl()),
                        dto.getRunDate(), dto.getDistance(), dto.getPace(), dto.getDuration(),
                        dto.getMemo(), dto.getCreatedAt(), dto.getCommentCtn(), dto.getLikeCtn()
                ))
                .collect(Collectors.toList());

        Long nextCursorId = hasNext ? result.get(result.size() - 1).getRunningLogId() : null;

        return new FeedScrollResponseDto(result, hasNext, nextCursorId);
    }
}
