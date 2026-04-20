package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.entity.FriendRequest;

import java.util.Optional;

public interface FriendRequestService {
    //TODO: receiverId, senderId로 친구 요청 조회
    Optional<FriendRequest> findByReceiverIdAndSenderId(Long receiverId, Long senderId);
}
