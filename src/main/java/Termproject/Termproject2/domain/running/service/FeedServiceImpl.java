package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.FeedScrollResponseDto;
import Termproject.Termproject2.domain.running.dto.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final RunningLogRepository runningLogRepository;

    public FeedScrollResponseDto getFriendFeeds(Long userId, Long cursorId, int size) {
        List<FriendFeedResponseDto> result = runningLogRepository.findFriendFeeds(userId, cursorId, size);

        boolean hasNext = result.size() > size;
        if (hasNext) {
            result = result.subList(0, size);
        }

        Long nextCursorId = hasNext ? result.get(result.size() - 1).getRunningLogId() : null;

        return new FeedScrollResponseDto(result, hasNext, nextCursorId);
    }
}
