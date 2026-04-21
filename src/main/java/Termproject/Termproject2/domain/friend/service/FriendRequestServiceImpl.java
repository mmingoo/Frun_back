package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.friend.repository.FriendRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendRequestServiceImpl implements FriendRequestService{

    private final FriendRequestRepository friendRequestRepository;

    //TODO: 수신자·발신자 ID로 친구 요청 조회
    @Override
    public Optional<FriendRequest> findByReceiverIdAndSenderId(Long receiverId, Long senderId) {
        return friendRequestRepository.findByReceiver_UserIdAndSender_UserId(receiverId, senderId);
    }

}
