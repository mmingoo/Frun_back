package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.dto.FriendResponseDto;

import java.util.List;

public interface FriendShipService {
    List<FriendResponseDto> getFriendList(Long userId);
}
