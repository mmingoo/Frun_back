// src/main/java/Termproject/Termproject2/domain/notification/service/NotificationService.java
package Termproject.Termproject2.domain.notification.service;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.notification.dto.reponse.NotificationDtos;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.user.entity.User;

import java.util.List;

public interface NotificationService {
    void notifyComment(User receiver, Comment comment);
    void deleteByComments(List<Comment> comments);
    void notifyFriendRequest(User receiver, FriendRequest friendRequest, User sender, FriendRequestStatus friendRequestStatus);
    void notifyLike(User receiver, User sender, RunningLog runningLog);

    long countByUserUserIdAndIsReadFalse(Long userId);

    NotificationDtos getNotificationList(Long userId, Long lastNotificationId, int size);

    void updateFriendRequestNotificationStatus(FriendRequest friendRequest, FriendRequestStatus status);
}
