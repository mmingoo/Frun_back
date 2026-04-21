package Termproject.Termproject2.domain.notification.repository;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.notification.entity.Notification;
import Termproject.Termproject2.domain.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
    //TODO: 미읽음 알림 수 조회
    long countByUserUserIdAndIsReadFalse(Long userId);

    //TODO: 특정 발신자·러닝일지·유형 조합의 알림 존재 여부 (중복 좋아요 알림 방지)
    boolean existsBySenderUserIdAndRunningLogRunningLogIdAndType(
            Long senderId, Long runningLogId, NotificationType type);

    //TODO: 유저의 알림 목록 커서 기반 조회 → NotificationRepositoryImpl (QueryDSL)

    //TODO: 특정 발신자·수신자·유형 조합의 최신 알림 조회 (친구 요청 상태 업데이트용)
    @Query("select n from Notification n where n.sender.userId = :senderUserId and n.user.userId = :userUserId and n.type = :type order by n.notificationId desc limit 1")
    Optional<Notification> findLatestBySenderUserIdAndUserUserIdAndType(@Param("senderUserId") Long senderUserId, @Param("userUserId") Long userUserId, @Param("type") NotificationType type);

    //TODO: 댓글 목록에 연관된 알림 삭제 (댓글 삭제 시 호출)
    @Modifying
    @Query("delete from Notification n where n.comment in :comments")
    void deleteByCommentIn(@Param("comments") List<Comment> comments);

    //TODO: 알림 읽음 처리
    @Modifying
    @Query("update Notification n set n.isRead = true where n.notificationId in :notificationIds")
    void updateIsReadToTrue(@Param("notificationIds") List<Long> notificationIds);

    //TODO: 유저와 관련된 모든 알림 삭제 (회원 탈퇴 시)
    // Native 사용 이유: JPQL은 multi-table DELETE 및 DELETE 절 내 LEFT JOIN을 지원하지 않음
    @Modifying
    @Query(value = """
            DELETE n FROM NOTIFICATION n
            LEFT JOIN RUNNING_LOG rl ON n.running_log_id = rl.running_log_id
            LEFT JOIN COMMENT c ON n.comment_id = c.comment_id
            LEFT JOIN COMMENT cp ON c.parent_id = cp.comment_id
            WHERE n.user_id = :userId
               OR n.sender_id = :userId
               OR rl.user_id = :userId
               OR c.user_id = :userId
               OR c.running_log_id IN (SELECT running_log_id FROM RUNNING_LOG WHERE user_id = :userId)
               OR cp.user_id = :userId
            """, nativeQuery = true)
    void deleteAllRelatedToUser(@Param("userId") Long userId);

    //TODO: 선택한 알림 삭제
    @Modifying
    @Query("delete from Notification n where n.notificationId in :ids and n.user.userId = :userId")
    void deleteSelectedNotification(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    //TODO: 유저의 전체 알림 삭제
    @Modifying
    @Query("delete from Notification n where n.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

}
