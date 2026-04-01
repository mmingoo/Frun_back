package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.friend.repository.FriendRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService{


    private final FriendRequestRepository friendRequestRepository;


    @Override
    public Optional<FriendRequest> findByReceiverIdAndSenderId(Long receiverId, Long senderId) {
        return friendRequestRepository.findByReceiver_UserIdAndSender_UserId(receiverId, senderId);
    }
}
