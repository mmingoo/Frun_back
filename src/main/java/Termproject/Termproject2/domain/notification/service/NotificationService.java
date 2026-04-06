// src/main/java/Termproject/Termproject2/domain/notification/service/NotificationService.java
package Termproject.Termproject2.domain.notification.service;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.user.entity.User;

public interface NotificationService {
    void notifyComment(User receiver, Comment comment);
    void notifyFriendRequest(User receiver, FriendRequest friendRequest);
    void notifyLike(User receiver, User sender, RunningLog runningLog);

    long countByUserUserIdAndIsReadFalse(Long userId);
}