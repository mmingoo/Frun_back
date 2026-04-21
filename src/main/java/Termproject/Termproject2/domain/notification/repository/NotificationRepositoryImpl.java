package Termproject.Termproject2.domain.notification.repository;

import Termproject.Termproject2.domain.notification.dto.reponse.NotificationDto;
import Termproject.Termproject2.domain.notification.entity.QNotification;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    //TODO: 유저의 알림 목록 커서 기반 조회 (발신자, 러닝일지, 댓글 정보 포함)
    @Override
    public List<NotificationDto> findNotificationByUserUserId(Long userId, Long cursorId, Pageable pageable) {
        QNotification n = QNotification.notification;

        return queryFactory
                .select(Projections.constructor(NotificationDto.class,
                        n.message,
                        n.isRead,
                        n.notificationId,
                        n.content,
                        n.sender.imageUrl,
                        n.sender.userId,
                        n.runningLog.runningLogId,
                        n.runningLog.user.userId,
                        n.comment.commentId,
                        n.friendRequestStatus,
                        n.sender.userStatus,
                        n.type))
                .from(n)
                // sender, runningLog, comment는 알림 유형에 따라 null일 수 있으므로 leftJoin
                .leftJoin(n.sender)
                .leftJoin(n.runningLog)
                .leftJoin(n.comment)
                .where(
                        n.user.userId.eq(userId),
                        // 커서 기반 페이지네이션: lastId 이전 항목만 조회
                        cursorId != null ? n.notificationId.lt(cursorId) : null
                )
                .orderBy(n.notificationId.desc()) // 최신 알림 우선 정렬
                .limit(pageable.getPageSize())    // 한 페이지 사이즈만큼 제한
                .fetch();
    }
}
