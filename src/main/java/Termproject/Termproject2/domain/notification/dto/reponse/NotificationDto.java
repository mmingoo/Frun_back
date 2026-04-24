package Termproject.Termproject2.domain.notification.dto.reponse;

import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.notification.entity.NotificationType;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 알림 응답 DTO
@Getter
@AllArgsConstructor
public class NotificationDto {
    private String message; // 알림 메시지
    private boolean isRead; // 읽음 여부
    private Long notificationId; // 알림 ID
    private String content; // 댓글 내용 미리보기 (댓글 알림 시)
    private String userProfileImageUrl; // 발신자 프로필 이미지 URL
    private Long senderId; // 발신자 ID
    private String senderNickname; // 발신자 현재 닉네임
    private Long runningLogId; // 관련 러닝일지 ID
    private Long authorId; // 러닝일지 작성자 ID
    private Long commentId; // 관련 댓글 ID
    private FriendRequestStatus friendRequestStatus; // 친구 요청 상태 (친구 요청 알림 시)
    private UserStatus senderStatus; // 발신자 계정 상태 (비활성 여부 마스킹용)
    private NotificationType type; // 알림 유형

    public void setProfileImageUrl(String userProfileImageUrl){
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
