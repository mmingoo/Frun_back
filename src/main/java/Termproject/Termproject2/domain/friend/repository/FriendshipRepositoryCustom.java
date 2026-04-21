package Termproject.Termproject2.domain.friend.repository;

import Termproject.Termproject2.domain.friend.dto.response.FriendResponseDto;
import Termproject.Termproject2.domain.friend.entity.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepositoryCustom {
    //TODO: 친구 목록 닉네임 오름차순 커서 기반 조회
    List<FriendResponseDto> getFriendList(Long userId, String cursorName, Long cursorId, int size);

}
