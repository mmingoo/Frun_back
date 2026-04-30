package Termproject.Termproject2.domain.comment.repository;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.comment.QComment;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    private final QComment comment = QComment.comment;

    //TODO: 최상위 댓글 커서 기반 조회
    @Override
    public List<Comment> findTopLevelComments(Long runningLogId, Long cursorId, int size){
        // 댓글 조회
        return jpaQueryFactory
                .selectFrom(comment)
                .join(comment.user).fetchJoin()
                .where(
                        comment.runningLog.runningLogId.eq(runningLogId),
                        comment.parent.isNull(),
                        cursorCondition(cursorId) // cursorId 가 없는 경우, 해당 조건이 무시됨
                )
                .orderBy(comment.commentId.asc())
                .limit(size + 1)
                .fetch();
    }

    //TODO: 여러 부모 댓글의 답글 수 일괄 집계
    @Override
    public Map<Long, Long> countRepliesByParentIds(List<Long> parentIds) {
        return jpaQueryFactory
                .select(comment.parent.commentId, comment.count())
                .from(comment)
                .where(comment.parent.commentId.in(parentIds)) // 전달받은 부모 ID들에 속한 댓글만 필터링
                .groupBy(comment.parent.commentId) // 부모 ID 별로 그룹화
                .fetch() // 쿼리 결과를 List<Tuple> 로 반환
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(comment.parent.commentId), // key : 부모 댓글 ID
                        tuple -> tuple.get(comment.count()) // value : 답글 갯수
                ));
    }

    //TODO: 특정 댓글의 답글 커서 기반 조회
    @Override
    public List<Comment> findReplies(Long parentId, Long cursorId, int size) {
        return jpaQueryFactory
                .selectFrom(comment)
                .join(comment.user).fetchJoin()
                .where(
                        comment.parent.commentId.eq(parentId),
                        cursorCondition(cursorId)
                )
                .orderBy(comment.commentId.asc())
                .limit(size + 1)
                .fetch();
    }


    // cursor 조건 (null이면 첫 페이지)
    private BooleanExpression cursorCondition(Long cursorId) {
        return cursorId != null ? comment.commentId.gt(cursorId) : null;
    }

}
