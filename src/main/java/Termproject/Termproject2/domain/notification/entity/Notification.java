package Termproject.Termproject2.domain.notification.entity;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.global.common.basedTime.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NOTIFICATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId; // 알림 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private NotificationType type; // 알림 유형 (COMMENT, LIKE, FRIEND_REQUEST 등)

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false; // 읽음 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user; // 알림 수신자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment; // 관련 댓글 (댓글 알림 시)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender; // 알림 발신자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_log_id")
    private RunningLog runningLog; // 관련 러닝일지

    @Enumerated(EnumType.STRING)
    @Column(name = "friend_request_status", length = 20)
    private FriendRequestStatus friendRequestStatus; // 친구 요청 상태 (친구 요청 알림 시)

    @Column(name = "message", length = 100, nullable = false)
    private String message; // 알림 메시지

    @Column(name = "content", length = 200)
    private String content; // 댓글 내용 미리보기 (댓글 알림 시)


    @Builder
    public Notification(User user, NotificationType type,
                        Comment comment, User sender, RunningLog runningLog, String message, String content,
                        FriendRequestStatus friendRequestStatus) {
        this.user = user;
        this.type = type;
        this.comment = comment;
        this.sender = sender;
        this.runningLog = runningLog;
        this.message = message;
        this.isRead = false;
        this.content = content;
        this.friendRequestStatus = friendRequestStatus;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void updateFriendRequestStatus(FriendRequestStatus status) {
        this.friendRequestStatus = status;
    }


}
