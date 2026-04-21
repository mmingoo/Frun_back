package Termproject.Termproject2.domain.notification.converter;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.notification.entity.Notification;
import Termproject.Termproject2.domain.notification.entity.NotificationType;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.user.entity.User;

public class NotificationConverter {

    //TODO: 댓글/답글 알림 엔티티로 변환
    public static Notification toCommentNotification(User receiver, Comment comment,
                                                      String message, String contentPreview) {
        return Notification.builder()
                .user(receiver)
                .type(NotificationType.COMMENT)
                .comment(comment)
                .sender(comment.getUser())
                .message(message)
                .content(contentPreview)
                .runningLog(comment.getRunningLog())
                .build();
    }

    //TODO: 친구 요청 알림 엔티티로 변환
    public static Notification toFriendRequestNotification(User receiver, User sender,
                                                            String message,
                                                            FriendRequestStatus status) {
        return Notification.builder()
                .user(receiver)
                .type(NotificationType.FRIEND_REQUEST)
                .sender(sender)
                .message(message)
                .friendRequestStatus(status)
                .build();
    }

    //TODO: 좋아요 알림 엔티티로 변환
    public static Notification toLikeNotification(User receiver, User sender,
                                                   RunningLog runningLog, String message) {
        return Notification.builder()
                .user(receiver)
                .sender(sender)
                .runningLog(runningLog)
                .type(NotificationType.LIKE)
                .message(message)
                .build();
    }

    //TODO: 친구 요청 수락 알림 엔티티로 변환
    public static Notification toFriendRequestAcceptedNotification(User receiver, User sender,
                                                                    String message) {
        return Notification.builder()
                .user(receiver)
                .type(NotificationType.FRIEND_REQUEST_ACCEPTED)
                .sender(sender)
                .message(message)
                .build();
    }

    //TODO: 친구 요청 거절 알림 엔티티로 변환
    public static Notification toFriendRequestRejectedNotification(User receiver, User sender,
                                                                    String message) {
        return Notification.builder()
                .user(receiver)
                .type(NotificationType.FRIEND_REQUEST_REJECTED)
                .sender(sender)
                .message(message)
                .build();
    }
}
