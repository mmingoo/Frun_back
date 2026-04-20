// src/main/java/Termproject/Termproject2/domain/notification/service/NotificationService.java
package Termproject.Termproject2.domain.notification.service;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.notification.dto.reponse.NotificationDtos;
import Termproject.Termproject2.domain.notification.dto.request.SelectedNotificationRequestDto;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.user.entity.User;

import java.util.List;

public interface NotificationService {
    //TODO: 댓글/답글 알림 생성
    void notifyComment(User receiver, Comment comment);

    //TODO: 댓글 삭제 시 연관 알림 삭제
    void deleteByComments(List<Comment> comments);

    //TODO: 친구 요청 알림 생성
    void notifyFriendRequest(User receiver, User sender, FriendRequestStatus friendRequestStatus);

    //TODO: 좋아요 알림 생성
    void notifyLike(User receiver, User sender, RunningLog runningLog);

    //TODO: 미읽음 알림 수 조회
    long countByUserUserIdAndIsReadFalse(Long userId);

    //TODO: 알림 목록 조회 (읽음 처리 포함)
    NotificationDtos getNotificationList(Long userId, Long lastNotificationId, int size);

    //TODO: 친구 요청 알림의 상태 업데이트
    void updateFriendRequestNotificationStatus(Long senderUserId, Long receiverUserId, FriendRequestStatus status);

    //TODO: 친구 요청 수락 알림 생성
    void notifyFriendRequestAccepted(User sender, User receiver);

    //TODO: 친구 요청 거절 알림 생성
    void notifyFriendRequestRejected(User sender, User receiver);

    //TODO: 선택한 알림 삭제
    void deleteSelectedNotification(Long userId, SelectedNotificationRequestDto selectedNotificationRequestDto);

    //TODO: 전체 알림 삭제
    void deleteAllNotification(Long userId);
}
