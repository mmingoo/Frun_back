package Termproject.Termproject2.domain.friend.converter;

import Termproject.Termproject2.domain.friend.dto.response.UserSearchResponse;
import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.friend.entity.Friendship;
import Termproject.Termproject2.domain.user.entity.User;

public class FriendConverter {

    //TODO: 친구 요청 객체 컨버터
    public static FriendRequest toFriendRequest(User sender, User receiver) {
        return FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.SENDED)
                .build();
    }

    //TODO: 친구 객체 생성 컨버터
    public static Friendship toFriendship(User senderUser, User receiveUser) {
        return Friendship.builder()
                .senderUser(senderUser)
                .receiveUser(receiveUser)
                .build();
    }

    //TODO: 친구 검색 결과 반환 컨버터
    public static UserSearchResponse toUserSearchResponse(User user, String profileImageUrl,
                                                          FriendRequestStatus friendStatus) {
        return UserSearchResponse.builder()
                .userId(user.getUserId())
                .nickname(user.getNickName())
                .profileImageUrl(profileImageUrl)
                .friendStatus(friendStatus)
                .build();
    }
}
