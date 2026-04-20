package Termproject.Termproject2.domain.comment;

import Termproject.Termproject2.domain.notification.entity.Notification;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.global.common.basedTime.BaseCreatedEntity;
import Termproject.Termproject2.global.common.basedTime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COMMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId; // 댓글 ID

    @Column(name = "content", nullable = false, length = 200)
    private String content; // 댓글 내용 (최대 200자)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_log_id", nullable = false)
    private RunningLog runningLog; // 댓글이 달린 러닝일지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user; // 댓글 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent; // 부모 댓글 (null이면 최상위 댓글)

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>(); // 답글 목록

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>(); // 이 댓글로 생성된 알림 목록


    @Builder
    public Comment(RunningLog runningLog, User user, String content, Comment parent) {
        this.runningLog = runningLog;
        this.user = user;
        this.content = content;
        this.parent = parent;
    }

    public void update(String content) {
        this.content = content;
    }

}
