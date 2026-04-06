package Termproject.Termproject2.domain.notification.dto.reponse;

import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
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

    public void setProfileImageUrl(String userProfileImageUrl){
        this.userProfileImageUrl = userProfileImageUrl;
    }
}
