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
    private Long notificationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "friend_request_id")
    private Long friendRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_log_id")
    private RunningLog runningLog;

    @Enumerated(EnumType.STRING)
    @Column(name = "friend_request_status", length = 20)
    private FriendRequestStatus friendRequestStatus;

    @Column(name = "message", length = 100)
    private String message;

    @Column(name = "content", length = 200)
    private String content;


    @Builder
    public Notification(User user, NotificationType type, Long friendRequestId,
                        Comment comment, User sender, RunningLog runningLog, String message, String content,
                        FriendRequestStatus friendRequestStatus) {
        this.user = user;
        this.type = type;
        this.friendRequestId = friendRequestId;
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
