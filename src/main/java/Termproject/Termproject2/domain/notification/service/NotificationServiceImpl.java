// src/main/java/Termproject/Termproject2/domain/notification/service/NotificationServiceImpl.java
package Termproject.Termproject2.domain.notification.service;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.notification.entity.Notification;
import Termproject.Termproject2.domain.notification.entity.NotificationType;
import Termproject.Termproject2.domain.notification.repository.NotificationRepository;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 댓글/답글 알림 생성
     * - receiver: 러닝일지 작성자 (알림을 받을 사람)
     * - comment: 작성된 댓글 또는 답글 엔티티
     * 주의: 본인이 본인 글에 댓글을 달 경우 알림을 보내지 않음
     */

    //TODO: 댓글/답글 알림 생성
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void notifyComment(User receiver, Comment comment) {
        // 댓글 작성자와 게시글 작성자가 동일한 경우 알림 생성 X
        if (receiver.getUserId().equals(comment.getUser().getUserId())) {
            return;
        }

        // 댓글/답글 알림 생성
        Notification notification = Notification.builder()
                .user(receiver)
                .type(NotificationType.COMMENT)
                .comment(comment)
                .sender(comment.getUser())
                .build();

        // 알림 저장
        notificationRepository.save(notification);
    }

    /**
     * 친구 요청 알림 생성
     * - receiver: 친구 요청을 받는 사람
     * - friendRequest: 생성된 친구 요청 엔티티
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void notifyFriendRequest(User receiver, FriendRequest friendRequest) {
        System.out.println("친구요청 알림 생성");
        // 친구 요청 알림 생성
        Notification notification = Notification.builder()
                .user(receiver)
                .type(NotificationType.FRIEND_REQUEST)
                .friendRequest(friendRequest)
                .build();

        // 친구 요청 알림 저장
        notificationRepository.save(notification);
    }

    /**
     * 좋아요 알림 생성
     * - receiver: 러닝일지 작성자 (알림을 받을 사람)
     * - sender: 좋아요를 누른 사람
     * - runningLog: 좋아요가 눌린 러닝일지
     * 주의: 본인 글에 본인이 좋아요 하는 경우는 LikeService에서 미리 필터링
     * 동일한 (sender, runningLog) 조합에 대해 좋아요 알림은 1번만 발송
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void notifyLike(User receiver, User sender, RunningLog runningLog) {
        // 이미 해당 게시글에 대한 좋아요 알림이 존재하면 스킵
        if (notificationRepository.existsBySenderUserIdAndRunningLogRunningLogIdAndType(
                sender.getUserId(), runningLog.getRunningLogId(), NotificationType.LIKE)) {
            return;
        }

        Notification notification = Notification.builder()
                .user(receiver)
                .sender(sender)
                .runningLog(runningLog)
                .type(NotificationType.LIKE)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public long countByUserUserIdAndIsReadFalse(Long userId) {
        return notificationRepository.countByUserUserIdAndIsReadFalse(userId);
    }
}