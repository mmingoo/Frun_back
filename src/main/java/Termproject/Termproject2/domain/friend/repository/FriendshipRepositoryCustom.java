package Termproject.Termproject2.domain.friend.repository;

import Termproject.Termproject2.domain.friend.dto.response.FriendResponseDto;
import Termproject.Termproject2.domain.friend.entity.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepositoryCustom {
    //TODO: 친구 목록 닉네임 오름차순 커서 기반 조회
    List<FriendResponseDto> getFriendList(Long userId, String cursorName, Long cursorId, int size);

    //TODO: 두 유저 간 친구 관계 조회
    Optional<Friendship> findByUserIdAndAuthorId(Long userId, Long authorId);

    //TODO: 친구 관계 양방향 삭제
    long deleteFriendship(Long myId, Long friendId);
}
