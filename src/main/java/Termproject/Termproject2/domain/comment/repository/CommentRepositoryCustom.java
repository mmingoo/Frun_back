package Termproject.Termproject2.domain.comment.repository;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.comment.dto.response.CommentResponse;
import Termproject.Termproject2.domain.comment.dto.response.CursorSliceResponse;
import Termproject.Termproject2.domain.comment.dto.response.ReplyResponse;

import java.util.List;
import java.util.Map;

public interface CommentRepositoryCustom {
    //TODO: 최상위 댓글 커서 기반 조회 (size+1개 조회로 hasNext 판단)
    List<Comment> findTopLevelComments(Long runningLogId, Long cursorId, int size);

    //TODO: 여러 부모 댓글의 답글 수 일괄 조회
    Map<Long, Long> countRepliesByParentIds(List<Long> parentIds);

    //TODO: 특정 댓글의 답글 커서 기반 조회
    List<Comment> findReplies(Long parentId, Long cursorId, int size);

    //TODO: 특정 러닝일지의 최상위 댓글 수 조회
    long countTopLevelComments(Long runningLogId);

    //TODO: 특정 댓글의 답글 수 조회
    long countReplies(Long parentId);
}
