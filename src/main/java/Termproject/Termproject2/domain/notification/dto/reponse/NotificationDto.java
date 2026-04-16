package Termproject.Termproject2.domain.notification.dto.reponse;

import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationDto {
    private String message;
    private boolean isRead;
    private Long notificationId;
    private String content;
    private String userProfileImageUrl;
    private Long senderId; // 알림 생성자 id
    private Long runningLogId;
    private Long authorId; // 러닝일지 작성자 id
    private Long commentId;
    private FriendRequestStatus friendRequestStatus; // 친구 요청 알림 시 PENDING or SENDED
    private UserStatus senderStatus; // 발신자 계정 상태

    public void setProfileImageUrl(String userProfileImageUrl){
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
