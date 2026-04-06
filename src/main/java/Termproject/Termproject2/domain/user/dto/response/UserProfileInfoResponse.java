package Termproject.Termproject2.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileInfoResponse {
    private Long userId;
    private String profileImageUrl;
    private String nickName;
    private long notificationCnt;
}
