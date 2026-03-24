package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final RunningLogRepository runningLogRepository;

    public List<FriendFeedResponseDto> getFriendFeeds(Long userId, int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        return runningLogRepository.findFriendFeeds(userId, pageable);
    }

}
