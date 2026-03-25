package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.dto.FriendResponseDto;
import Termproject.Termproject2.domain.friend.entity.Friendship;
import Termproject.Termproject2.domain.friend.repository.FriendshipRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendshipServiceImpl implements FriendShipService{


    private FriendshipRepository friendshipRepository;
    public List<FriendResponseDto> getFriendList(Long userId) {
        return null;
    }
}
