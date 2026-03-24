package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.dto.FriendFeedResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RunningLogRepositoryCustom {
    List<FriendFeedResponseDto> findFriendFeeds(Long userId, Pageable pageable);
}