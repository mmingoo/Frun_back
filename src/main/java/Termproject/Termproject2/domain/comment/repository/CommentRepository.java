package Termproject.Termproject2.domain.comment.repository;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.comment.dto.response.CommentResponse;
import Termproject.Termproject2.domain.comment.dto.response.CursorSliceResponse;
import Termproject.Termproject2.domain.comment.dto.response.ReplyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

public interface CommentRepository extends JpaRepository<Comment, Long> , CommentRepositoryCustom{
    List<Comment> findTopLevelComments(Long runningLogId, Long cursorId, int size);

    List<Comment> findReplies(Long parentId, Long cursorId, int size);
    Map<Long, Long> countRepliesByParentIds(List<Long> parentIds);

}
