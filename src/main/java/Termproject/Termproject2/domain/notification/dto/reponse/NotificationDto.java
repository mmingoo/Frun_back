package Termproject.Termproject2.domain.notification.dto.reponse;

import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.notification.entity.NotificationType;
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
    private Long senderId;
    private Long runningLogId;
    private Long authorId;
    private Long commentId;
    private FriendRequestStatus friendRequestStatus;
    private UserStatus senderStatus;
    private NotificationType type;

    public void setProfileImageUrl(String userProfileImageUrl){
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
