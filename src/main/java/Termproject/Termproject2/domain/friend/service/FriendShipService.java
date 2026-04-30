package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.dto.request.FriendRequestDto;
import Termproject.Termproject2.domain.friend.dto.response.FriendListResponse;
import Termproject.Termproject2.domain.friend.dto.response.UserSearchListResponse;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;

public interface FriendShipService {
    //TODO: 친구 목록 커서 기반 조회
    FriendListResponse getFriendList(Long userId, String cursorName, int size);

    //TODO: 닉네임 키워드로 유저 검색 (친구 상태 포함)
    UserSearchListResponse searchUsersWithDetailStatus(Long currentUserId, String keyword, String cursorName, int size);

    //TODO: 친구 삭제
    void unfriend(Long myId, Long friendId);

    //TODO: 친구 요청 전송
    void sendFriendRequest(Long userId, Long friendId);

    //TODO: 친구 요청 수락
    void acceptFriendRequest(Long senderId, Long userId);

    //TODO: 친구 요청 거절
    void rejectFriendRequest(Long senderId, Long userId);

    //TODO: 친구 수 조회
    long getFriendCount(Long targetUserId);

    //TODO: 두 유저 간 친구 관계 상태 반환
    FriendRequestStatus getStatus(Long me, Long other);
}
