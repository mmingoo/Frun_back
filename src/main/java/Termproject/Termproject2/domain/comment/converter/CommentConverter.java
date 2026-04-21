package Termproject.Termproject2.domain.comment.converter;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.user.entity.User;

public class CommentConverter {

    //TODO: 댓글로 변환하는 컨버터
    public static Comment toComment(RunningLog runningLog, User user, String content) {
        return Comment.builder()
                .runningLog(runningLog)
                .user(user)
                .content(content)
                .build();
    }

    //TODO: 답글로 변환하는 컨버터
    public static Comment toReply(RunningLog runningLog, User user, String content, Comment parent) {
        return Comment.builder()
                .runningLog(runningLog)
                .user(user)
                .content(content)
                .parent(parent)
                .build();
    }
}
