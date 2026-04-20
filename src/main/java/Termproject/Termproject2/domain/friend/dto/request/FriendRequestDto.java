package Termproject.Termproject2.domain.friend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// 친구 요청 생성 요청
public class FriendRequestDto {
    private Long senderId; // 요청 보낸 유저 ID
    private Long receiverId; // 요청 받은 유저 ID
}