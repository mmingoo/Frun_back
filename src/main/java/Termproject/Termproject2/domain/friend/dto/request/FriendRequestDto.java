package Termproject.Termproject2.domain.friend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FriendRequestDto {
    private Long senderId;
    private Long receiverId;
}