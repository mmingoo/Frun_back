package Termproject.Termproject2.domain.notification.repository;

import Termproject.Termproject2.domain.notification.entity.Notification;
import Termproject.Termproject2.domain.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByUserUserIdAndIsReadFalse(Long userId);

    boolean existsBySenderUserIdAndRunningLogRunningLogIdAndType(
            Long senderId, Long runningLogId, NotificationType type);
}
