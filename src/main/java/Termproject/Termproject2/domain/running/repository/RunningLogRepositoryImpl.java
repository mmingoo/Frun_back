package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.friend.entity.QFriendship;
import Termproject.Termproject2.domain.report.QReport;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedResponseDto;
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
                        runningLog.runTime,
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

    // 유저 페이지 피드 조회 (본인이면 비공개 포함, 타인이면 공개만)
    @Override
    public List<MyPageFeedResponseDto> findUserPageFeeds(Long userId, Long cursorId, int size, boolean isOwner) {
        QRunningLog runningLog = QRunningLog.runningLog;

        return queryFactory
                .select(Projections.constructor(MyPageFeedResponseDto.class,
                        runningLog.user.userId,
                        runningLog.runningLogId,
                        runningLog.runDate,
                        runningLog.distance,
                        runningLog.pace,
                        runningLog.duration,
                        runningLog.likeCtn,
                        runningLog.commentCtn,
                        runningLog.memo))
                .from(runningLog)
                .where(
                        runningLog.user.userId.eq(userId),
                        runningLog.isDeleted.isFalse(),
                        isOwner ? null : runningLog.isPublic.isTrue(),
                        cursorId != null ? runningLog.runningLogId.lt(cursorId) : null
                )
                .orderBy(runningLog.createdAt.desc())
                .limit(size + 1)
                .fetch();
    }

    @Override
    public Map<Long, List<String>> findImagesByRunningLogIds(List<Long> runningLogIds) {
        // runningLogIds 존재 여부
        if (runningLogIds == null || runningLogIds.isEmpty()) {
            return Collections.emptyMap();
        }


        QRunningLogImage image = QRunningLogImage.runningLogImage;

        // (러닝로그 id, imageUrl) 튜플 형태
        List<Tuple> tuples = queryFactory
                .select(image.runningLog.runningLogId, image.imageUrl)
                .from(image)
                .where(image.runningLog.runningLogId.in(runningLogIds))
                .fetch();

        // runningLogId 기준으로 그룹핑해서, 각 로그에 해당하는 imageUrl 리스트를 만듦
        return tuples.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(image.runningLog.runningLogId), // logId 기준으로 매핑
                        Collectors.mapping(t -> t.get(image.imageUrl), Collectors.toList())
                ));
    }

    @Override
    public Map<Long, String> findImageByRunningLogIds(List<Long> runningLogIds) {

        QRunningLogImage image = QRunningLogImage.runningLogImage;

        // runningLogIds 존재 여부 확인
        if (runningLogIds == null || runningLogIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Tuple> tuples = queryFactory
                .select(image.runningLog.runningLogId, image.imageUrl)
                .from(image)
                .where(image.runningLog.runningLogId.in(runningLogIds))
                // 첫번째 이미지 1개
                .orderBy(image.runningLog.runningLogId.asc(), image.createdAt.asc())
                .fetch();

        // runningLogId -> imageUrl (하나의 사진만 유지)
        return tuples.stream()
                .filter(t -> t.get(image.runningLog.runningLogId) != null)
                .collect(Collectors.toMap(
                        t -> t.get(image.runningLog.runningLogId),
                        t -> t.get(image.imageUrl),
                        (existing, replacement) -> existing // 첫 번째 값 유지
                ));
    }


}
