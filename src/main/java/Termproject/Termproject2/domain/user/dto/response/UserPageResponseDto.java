package Termproject.Termproject2.domain.user.dto.response;

import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPageResponseDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String bio;
    private long friendCount;
    private boolean isOwner;
    private FriendRequestStatus friendRequestStatus;
    private long totalRunCount;
    private double totalDistanceKm;
    private String avgPace;
}
