package Termproject.Termproject2.domain.notification.repository;

import Termproject.Termproject2.domain.friend.entity.FriendRequest;
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
            "c.commentId, n.friendRequestStatus) " +
        "from Notification n " +
        "left join n.sender s " +
        "left join n.runningLog rl " +
        "left join n.comment c " +
        "where n.user.userId = :userId " +
        "and (:lastId is null or n.notificationId < :lastId) " +
        "order by n.notificationId desc")
    List<NotificationDto> findByUserUserId(@Param("userId") Long userId, @Param("lastId") Long lastId, Pageable pageable);

    Optional<Notification> findByFriendRequest(FriendRequest friendRequest);

    @Modifying
    @Query("update Notification n set n.isRead = true where n.notificationId in :notificationIds")
    void updateIsReadToTrue(@Param("notificationIds") List<Long> notificationIds);
}
