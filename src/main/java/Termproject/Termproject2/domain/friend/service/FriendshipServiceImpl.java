package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.dto.FriendListResponse;
import Termproject.Termproject2.domain.friend.dto.FriendResponseDto;
import Termproject.Termproject2.domain.friend.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendShipService {

    private final FriendshipRepository friendshipRepository;

    @Override
    public FriendListResponse getFriendList(Long userId, String cursorName, Long cursorId, int size) {
        List<FriendResponseDto> results = friendshipRepository.getFriendList(userId, cursorName, cursorId, size);


        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        FriendResponseDto last = hasNext ? results.get(results.size() - 1) : null;
        return new FriendListResponse(results, hasNext,
                last != null ? last.getFriendId() : null,
                last != null ? last.getFriendName() : null);
    }
}
