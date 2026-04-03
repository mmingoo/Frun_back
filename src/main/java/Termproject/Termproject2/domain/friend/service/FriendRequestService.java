package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.entity.FriendRequest;

import java.util.Optional;

public interface FriendRequestService {
    Optional<FriendRequest> findByReceiverIdAndSenderId(Long receiverId, Long senderId);

}
