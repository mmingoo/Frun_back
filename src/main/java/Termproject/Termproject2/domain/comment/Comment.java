package Termproject.Termproject2.domain.comment;

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
    private Long commentId;

    @Lob
    @Column(name = "content", nullable = false, length = 200)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_log_id", nullable = false)
    private RunningLog runningLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();


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
