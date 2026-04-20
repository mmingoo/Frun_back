package Termproject.Termproject2.domain.notification.repository;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.notification.dto.reponse.NotificationDto;
import Termproject.Termproject2.domain.notification.entity.Notification;
import Termproject.Termproject2.domain.notification.entity.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByUserUserIdAndIsReadFalse(Long userId);

    boolean existsBySenderUserIdAndRunningLogRunningLogIdAndType(
            Long senderId, Long runningLogId, NotificationType type);

    @Query("select new Termproject.Termproject2.domain.notification.dto.reponse.NotificationDto(" +
        "n.message, n.isRead, n.notificationId, n.content, " +
            "s.imageUrl, s.userId, rl.runningLogId, " +
            "rl.user.userId, " +
            "c.commentId, n.friendRequestStatus, s.userStatus, n.type) " +
        "from Notification n " +
        "left join n.sender s " +
        "left join n.runningLog rl " +
        "left join n.comment c " +
        "where n.user.userId = :userId " +
        "and (:lastId is null or n.notificationId < :lastId) " +
        "order by n.notificationId desc")
    List<NotificationDto> findByUserUserId(@Param("userId") Long userId, @Param("lastId") Long lastId, Pageable pageable);

    Optional<Notification> findByFriendRequestId(Long friendRequestId);

    @Modifying
    @Query("delete from Notification n where n.comment in :comments")
    void deleteByCommentIn(@Param("comments") List<Comment> comments);

    @Modifying
    @Query("update Notification n set n.isRead = true where n.notificationId in :notificationIds")
    void updateIsReadToTrue(@Param("notificationIds") List<Long> notificationIds);

    @Modifying
    @Query(value = """
            DELETE n FROM NOTIFICATION n
            LEFT JOIN RUNNING_LOG rl ON n.running_log_id = rl.running_log_id
            LEFT JOIN COMMENT c ON n.comment_id = c.comment_id
            LEFT JOIN COMMENT cp ON c.parent_id = cp.comment_id
            LEFT JOIN FRIEND_REQUEST fr ON n.friend_request_id = fr.friend_request_id
            WHERE n.user_id = :userId
               OR n.sender_id = :userId
               OR rl.user_id = :userId
               OR c.user_id = :userId
               OR c.running_log_id IN (SELECT running_log_id FROM RUNNING_LOG WHERE user_id = :userId)
               OR cp.user_id = :userId
               OR fr.sender_id = :userId
               OR fr.receiver_id = :userId
            """, nativeQuery = true)
    void deleteAllRelatedToUser(@Param("userId") Long userId);
}
