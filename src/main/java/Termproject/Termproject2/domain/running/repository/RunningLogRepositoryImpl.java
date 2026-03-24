package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.friend.QFriendship;
import Termproject.Termproject2.domain.report.QReport;
import Termproject.Termproject2.domain.running.dto.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.entity.QRunningLog;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class RunningLogRepositoryImpl implements RunningLogRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<FriendFeedResponseDto> findFriendFeeds(Long userId, Pageable pageable) {


        QRunningLog runningLog = QRunningLog.runningLog;
        QFriendship friendship = QFriendship.friendship;
        QReport report = QReport.report;
        return queryFactory
                .select(Projections.constructor(FriendFeedResponseDto.class,
                        runningLog.runningLogId,
                        runningLog.user.userId,
                        runningLog.user.nickName,
                        runningLog.user.imageUrl,
                        runningLog.runDate,
                        runningLog.distance,
                        runningLog.pace,
                        runningLog.duration,
                        runningLog.memo,
                        runningLog.createdAt))
                .from(runningLog)
                .join(friendship)
                .on(
                        (friendship.id.senderUserId.eq(userId)
                                .and(friendship.id.receiveUserId.eq(runningLog.user.userId)))
                                .or(friendship.id.receiveUserId.eq(userId)
                                        .and(friendship.id.senderUserId.eq(runningLog.user.userId)))

                )
                .where(
                        // 삭제되지 않은 러닝 일지일 것
                        runningLog.isDeleted.isFalse(),

                        // 공개된 러닝일지 일 것
                        runningLog.isPublic.isTrue(),

                        // 신고 접수되지 않은 러닝일지일 것
                        JPAExpressions.selectOne()
                                .from(report)
                                .where(report.status.eq("COMPLETED"),
                                        report.runningLog.runningLogId.eq(runningLog.runningLogId)


                                )
                                .notExists()

                )
                .orderBy(runningLog.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }


}