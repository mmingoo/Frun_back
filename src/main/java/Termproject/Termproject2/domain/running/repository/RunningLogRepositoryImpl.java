package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.comment.QComment;
import Termproject.Termproject2.domain.friend.entity.QFriendship;
import Termproject.Termproject2.domain.report.entity.QReport;
import Termproject.Termproject2.domain.report.entity.ReportStatus;
import Termproject.Termproject2.domain.running.dto.request.FeedSortType;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedResponseDto;
import Termproject.Termproject2.domain.running.entity.QRunningLog;
import Termproject.Termproject2.domain.running.entity.QRunningLogImage;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RunningLogRepositoryImpl implements RunningLogRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    //TODO: 친구 피드 목록 커서 기반 조회 (공개·활성·미신고 로그만)
    @Override
    public List<FriendFeedResponseDto> findFriendFeeds(Long userId, Long cursorId, int size) {

        QRunningLog runningLog = QRunningLog.runningLog;
        QFriendship friendship = QFriendship.friendship;
        QReport report = QReport.report;
        QComment comment = QComment.comment;

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

                        // 서브쿼리: 해당 로그의 댓글 개수 조회
                        JPAExpressions.select(comment.count().intValue())
                                .from(comment)
                                .where(comment.runningLog.runningLogId.eq(runningLog.runningLogId)),
                        runningLog.likeCtn))

                .from(runningLog)
                // 친구 관계 조인 (내가 발신자 혹은 수신자인 경우 모두 포함)
                .join(friendship)
                .on(
                        (friendship.id.senderUserId.eq(userId)
                                .and(friendship.id.receiveUserId.eq(runningLog.user.userId)))
                                .or(friendship.id.receiveUserId.eq(userId)
                                        .and(friendship.id.senderUserId.eq(runningLog.user.userId)))
                )
                .where(
                        runningLog.isDeleted.isFalse(), // 삭제 여부 체크
                        runningLog.isPublic.isTrue(),  // 공개 여부 체크
                        runningLog.user.userStatus.eq(UserStatus.ACTIVE), // 사용자 활성화 상태 체크
                        cursorId != null ? runningLog.runningLogId.lt(cursorId) : null, // 페이징 처리
                        // 서브쿼리: 신고 처리가 완료된 게시물인지 확인 (NOT EXISTS)
                        JPAExpressions.selectOne()
                                .from(report)
                                .where(report.status.eq(ReportStatus.COMPLETED),
                                        report.runningLog.runningLogId.eq(runningLog.runningLogId))
                                .notExists()
                )
                .orderBy(runningLog.createdAt.desc()) // 최신순 정렬
                .limit(size + 1) // 다음 페이지 존재 확인을 위해 1개 더 조회
                .fetch();
    }

    //TODO: 마이페이지·유저페이지 피드 커서 기반 조회 (본인이면 비공개 포함, 정렬 지원)
    @Override
    public List<MyPageFeedResponseDto> findUserPageFeeds(Long userId, Long cursorId, String cursorValue, int size, boolean isOwner, FeedSortType sortType) {
        QRunningLog runningLog = QRunningLog.runningLog;
        QComment comment = QComment.comment;

        return queryFactory
                .select(Projections.constructor(MyPageFeedResponseDto.class,
                        runningLog.user.userId,
                        runningLog.runningLogId,
                        runningLog.runDate,
                        runningLog.distance,
                        runningLog.pace,
                        runningLog.duration,
                        runningLog.likeCtn,

                        // 서브쿼리: 해당 로그의 댓글 개수 조회
                        JPAExpressions.select(comment.count().intValue())
                                .from(comment)
                                .where(comment.runningLog.runningLogId.eq(runningLog.runningLogId)),
                        runningLog.memo,
                        runningLog.runTime,
                        runningLog.paceSeconds))
                .from(runningLog)
                .where(
                        runningLog.user.userId.eq(userId),
                        runningLog.isDeleted.isFalse(),
                        isOwner ? null : runningLog.isPublic.isTrue(),
                        isOwner ? null : runningLog.user.userStatus.eq(UserStatus.ACTIVE),
                        buildCursorCondition(runningLog, sortType, cursorId, cursorValue)
                )
                .orderBy(buildOrderSpecifier(runningLog, sortType), runningLog.runningLogId.desc())
                .limit(size + 1)
                .fetch();
    }

    // 정렬 기준에 따른 OrderSpecifier 반환
    private OrderSpecifier<?> buildOrderSpecifier(QRunningLog log, FeedSortType sortType) {
        return switch (sortType) {
            case RUN_DATE  -> log.runDate.desc();
            case RUN_TIME  -> log.duration.desc().nullsLast();
            case DISTANCE  -> log.distance.desc();
            case PACE      -> log.paceSeconds.asc().nullsLast();
            default        -> log.createdAt.desc(); // CREATED_AT
        };
    }

    // 정렬 기준과 커서 값에 따른 커서 조건 반환 (복합 커서: 정렬값 + ID 타이브레이킹)
    private BooleanExpression buildCursorCondition(QRunningLog log, FeedSortType sortType, Long cursorId, String cursorValue) {
        if (cursorId == null) return null;

        // where runningLogId < cursorId
        BooleanExpression idLt = log.runningLogId.lt(cursorId);

        if (cursorValue == null) return idLt;

        return switch (sortType) {
            // sortType 이 CREATED_AT 인 경우
            // WHERE runningLogId < cursorId
            case CREATED_AT -> idLt;

            // sortType 이 CREATED_AT 인 경우
            //WHERE run_date < :date OR (run_date = :date AND running_log_id < :cursorId)
            case RUN_DATE -> {
                LocalDate date = LocalDate.parse(cursorValue);
                yield log.runDate.lt(date)
                        .or(log.runDate.eq(date).and(idLt));
            }
            // sortType 이 CREATED_AT 인 경우
            // WHERE run_time > :time OR (run_time = :time AND running_log_id < :cursorId)
            case RUN_TIME -> {
                LocalTime time = LocalTime.parse(cursorValue);
                // ASC 정렬: 커서보다 늦은 시간 OR 같은 시간에서 ID 타이브레이킹
                yield log.runTime.gt(time)
                        .or(log.runTime.eq(time).and(idLt));
            }
            // sortType 이 CREATED_AT 인 경우
            // WHERE distance < :dist OR (distance = :dist AND running_log_id < :cursorId)
            case DISTANCE -> {
                BigDecimal dist = new BigDecimal(cursorValue);
                // DESC 정렬: 커서보다 짧은 거리 OR 같은 거리에서 ID 타이브레이킹
                yield log.distance.lt(dist)
                        .or(log.distance.eq(dist).and(idLt));
            }

            // sortType 이 CREATED_AT 인 경우
            // WHERE pace_seconds > :ps OR (pace_seconds = :ps AND running_log_id < :cursorId)
            case PACE -> {
                int ps = Integer.parseInt(cursorValue);
                // ASC 정렬: 커서보다 느린 페이스(초 큰 값) OR 같은 페이스에서 ID 타이브레이킹
                yield log.paceSeconds.gt(ps)
                        .or(log.paceSeconds.eq(ps).and(idLt));
            }
        };
    }

    //TODO: 러닝일지 ID 목록으로 전체 이미지 URL 조회 (Map<logId, List<url>>)
    @Override
    public Map<Long, List<String>> findImagesByRunningLogIds(List<Long> runningLogIds) {
        // runningLogIds 유효성 검사
        if (runningLogIds == null || runningLogIds.isEmpty()) {
            return Collections.emptyMap();
        }

        QRunningLogImage image = QRunningLogImage.runningLogImage;

        List<Tuple> tuples = queryFactory
                .select(image.runningLog.runningLogId, image.imageUrl) // runningLog 별 image 조회
                .from(image)
                .where(image.runningLog.runningLogId.in(runningLogIds)) // 러닝일지에 해당하는 이미지 조회 조건
                .fetch();

        // 조회된 튜플 리스트를 RunningLogId 기준으로 그룹화하여 Map으로 변환
        return tuples.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(image.runningLog.runningLogId), // runningLogId 별 이미지 맵핑
                        Collectors.mapping(t -> t.get(image.imageUrl), Collectors.toList())
                ));
    }

    //TODO: 러닝일지 ID 목록으로 대표 이미지 URL 1개씩 조회 (Map<logId, url>)
    @Override
    public Map<Long, String> findImageByRunningLogIds(List<Long> runningLogIds) {

        QRunningLogImage image = QRunningLogImage.runningLogImage;

        if (runningLogIds == null || runningLogIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Tuple> tuples = queryFactory
                .select(image.runningLog.runningLogId, image.imageUrl)
                .from(image)
                // 러닝로그에 해당 하는 이미지일 것
                .where(image.runningLog.runningLogId.in(runningLogIds))
                // 생성일 순으로 정렬하여 첫 번째 이미지를 결정할 기준 마련
                .orderBy(image.runningLog.runningLogId.asc(), image.createdAt.asc())
                .fetch();

        // 로그 ID당 하나의 대표 이미지만 매핑(썸네일 결정) (중복 발생 시 기존 값 유지)
        return tuples.stream()
                .filter(t -> t.get(image.runningLog.runningLogId) != null)
                .collect(Collectors.toMap(
                        t -> t.get(image.runningLog.runningLogId),
                        t -> t.get(image.imageUrl),
                        (existing, replacement) -> existing
                )); // imageUrl 중복될 시 기존 거 선택
    }
}