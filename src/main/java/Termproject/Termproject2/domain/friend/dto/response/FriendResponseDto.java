package Termproject.Termproject2.domain.friend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 친구 단건 응답
public class FriendResponseDto {

    private Long friendId; // 친구 유저 ID
    private String friendName; // 친구 닉네임
    private String friendProfileImage; // 친구 프로필 이미지 URL

}
