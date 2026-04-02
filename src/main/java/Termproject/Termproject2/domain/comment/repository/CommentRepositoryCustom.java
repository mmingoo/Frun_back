package Termproject.Termproject2.domain.comment.repository;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.comment.dto.response.CommentResponse;
import Termproject.Termproject2.domain.comment.dto.response.CursorSliceResponse;
import Termproject.Termproject2.domain.comment.dto.response.ReplyResponse;

import java.util.List;
import java.util.Map;

public interface CommentRepositoryCustom {
    List<Comment> findTopLevelComments(Long runningLogId, Long cursorId, int size);
    Map<Long, Long> countRepliesByParentIds(List<Long> parentIds);
    List<Comment> findReplies(Long parentId, Long cursorId, int size);
    long countTopLevelComments(Long runningLogId);
    long countReplies(Long parentId);
}
