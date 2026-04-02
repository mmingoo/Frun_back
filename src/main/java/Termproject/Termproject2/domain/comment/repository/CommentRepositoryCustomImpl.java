package Termproject.Termproject2.domain.comment.repository;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.comment.QComment;
import Termproject.Termproject2.domain.comment.dto.response.CommentResponse;
import Termproject.Termproject2.domain.comment.dto.response.CursorSliceResponse;
import Termproject.Termproject2.domain.comment.dto.response.ReplyResponse;
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
    private final QComment reply = new QComment("reply"); // 답글 수 서브쿼리용

    //TODO: 댓글 상조회
    @Override
    public List<Comment> findTopLevelComments(Long runningLogId, Long cursorId, int size){
        // 댓글 조회
        List<Comment> comments = jpaQueryFactory
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
        return comments;
    }

    @Override
    public Map<Long, Long> countRepliesByParentIds(List<Long> parentIds) {
        return jpaQueryFactory
                .select(comment.parent.commentId, comment.count())
                .from(comment)
                .where(comment.parent.commentId.in(parentIds))
                .groupBy(comment.parent.commentId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(comment.parent.commentId),
                        tuple -> tuple.get(comment.count())
                ));
    }

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


    @Override
    public long countTopLevelComments(Long runningLogId) {
        Long count = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(
                        comment.runningLog.runningLogId.eq(runningLogId),
                        comment.parent.isNull()
                )
                .fetchOne();
        return count != null ? count : 0L;
    }

    @Override
    public long countReplies(Long parentId) {
        Long count = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.parent.commentId.eq(parentId))
                .fetchOne();
        return count != null ? count : 0L;
    }

    // cursor 조건 (null이면 첫 페이지)
    private BooleanExpression cursorCondition(Long cursorId) {
        return cursorId != null ? comment.commentId.gt(cursorId) : null;
    }

}
