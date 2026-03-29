package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.friend.entity.QFriendship;
import Termproject.Termproject2.domain.report.QReport;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.entity.QRunningLog;
import Termproject.Termproject2.domain.running.entity.QRunningLogImage;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RunningLogRepositoryImpl implements RunningLogRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<FriendFeedResponseDto> findFriendFeeds(Long userId, Long cursorId, int size) {

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
                        runningLog.createdAt,
                        runningLog.commentCtn,
                        runningLog.likeCtn))
                .from(runningLog)
                .join(friendship)
                .on(
                        (friendship.id.senderUserId.eq(userId)
                                .and(friendship.id.receiveUserId.eq(runningLog.user.userId)))
                                .or(friendship.id.receiveUserId.eq(userId)
                                        .and(friendship.id.senderUserId.eq(runningLog.user.userId)))
                )
                .where(
                        runningLog.isDeleted.isFalse(),
                        runningLog.isPublic.isTrue(),
                        cursorId != null ? runningLog.runningLogId.lt(cursorId) : null,
                        JPAExpressions.selectOne()
                                .from(report)
                                .where(report.status.eq("COMPLETED"),
                                        report.runningLog.runningLogId.eq(runningLog.runningLogId))
                                .notExists()
                )
                .orderBy(runningLog.createdAt.desc())
                .limit(size + 1)
                .fetch();
    }

    @Override
    public Map<Long, List<String>> findImagesByRunningLogIds(List<Long> runningLogIds) {
        if (runningLogIds == null || runningLogIds.isEmpty()) {
            return Collections.emptyMap();
        }

        QRunningLogImage image = QRunningLogImage.runningLogImage;

        List<Tuple> tuples = queryFactory
                .select(image.runningLog.runningLogId, image.imageUrl)
                .from(image)
                .where(image.runningLog.runningLogId.in(runningLogIds))
                .fetch();

        return tuples.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(image.runningLog.runningLogId),
                        Collectors.mapping(t -> t.get(image.imageUrl), Collectors.toList())
                ));
    }


}
