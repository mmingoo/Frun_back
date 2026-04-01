package Termproject.Termproject2.domain.friend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendResponseDto {

    private Long friendId;
    private String friendName;
    private String friendProfileImage;

}
