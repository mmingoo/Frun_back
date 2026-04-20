package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.comment.QComment;
import Termproject.Termproject2.domain.friend.entity.QFriendship;
import Termproject.Termproject2.domain.report.entity.QReport;
import Termproject.Termproject2.domain.report.entity.ReportStatus;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.MyPageFeedResponseDto;
import Termproject.Termproject2.domain.running.entity.QRunningLog;
import Termproject.Termproject2.domain.running.entity.QRunningLogImage;
import Termproject.Termproject2.domain.user.entity.UserStatus;
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

    //TODO: 마이페이지·유저페이지 피드 커서 기반 조회 (본인이면 비공개 포함)
    @Override
    public List<MyPageFeedResponseDto> findUserPageFeeds(Long userId, Long cursorId, int size, boolean isOwner) {
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
                        runningLog.memo))
                .from(runningLog)
                .where(
                        runningLog.user.userId.eq(userId),
                        runningLog.isDeleted.isFalse(),
                        // 소유자 여부에 따른 조건부 필터링
                        isOwner ? null : runningLog.isPublic.isTrue(),
                        isOwner ? null : runningLog.user.userStatus.eq(UserStatus.ACTIVE),
                        cursorId != null ? runningLog.runningLogId.lt(cursorId) : null
                )
                .orderBy(runningLog.createdAt.desc())
                .limit(size + 1)
                .fetch();
    }

    //TODO: 러닝일지 ID 목록으로 전체 이미지 URL 조회 (Map<logId, List<url>>)
    @Override
    public Map<Long, List<String>> findImagesByRunningLogIds(List<Long> runningLogIds) {
        // 파라미터 유효성 검사
        if (runningLogIds == null || runningLogIds.isEmpty()) {
            return Collections.emptyMap();
        }

        QRunningLogImage image = QRunningLogImage.runningLogImage;

        List<Tuple> tuples = queryFactory
                .select(image.runningLog.runningLogId, image.imageUrl)
                .from(image)
                .where(image.runningLog.runningLogId.in(runningLogIds))
                .fetch();

        // 조회된 튜플 리스트를 RunningLogId 기준으로 그룹화하여 Map으로 변환
        return tuples.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(image.runningLog.runningLogId),
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
                .where(image.runningLog.runningLogId.in(runningLogIds))
                // 생성일 순으로 정렬하여 첫 번째 이미지를 결정할 기준 마련
                .orderBy(image.runningLog.runningLogId.asc(), image.createdAt.asc())
                .fetch();

        // 로그 ID당 하나의 대표 이미지만 매핑 (중복 발생 시 기존 값 유지)
        return tuples.stream()
                .filter(t -> t.get(image.runningLog.runningLogId) != null)
                .collect(Collectors.toMap(
                        t -> t.get(image.runningLog.runningLogId),
                        t -> t.get(image.imageUrl),
                        (existing, replacement) -> existing
                ));
    }
}