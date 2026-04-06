package Termproject.Termproject2.domain.notification.entity;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_request_id")
    private FriendRequest friendRequest;

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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Notification(User user, NotificationType type, FriendRequest friendRequest, Comment comment, User sender, RunningLog runningLog) {
        this.user = user;
        this.type = type;
        this.friendRequest = friendRequest;
        this.comment = comment;
        this.sender = sender;
        this.runningLog = runningLog;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
