package Termproject.Termproject2.domain.comment.repository;

import Termproject.Termproject2.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> , CommentRepositoryCustom{

    //TODO: 특정 러닝일지의 댓글 수 조회
    long countByRunningLogRunningLogId(Long runningLogId);

    //TODO: 댓글과 러닝일지·작성자 정보를 fetch join으로 함께 조회
    @Query("SELECT c FROM Comment c JOIN FETCH c.runningLog rl JOIN FETCH rl.user WHERE c.commentId = :commentId")
    Optional<Comment> findByIdWithRunningLogOwner(@Param("commentId") Long commentId);

    //TODO: 유저 댓글에 달린 다른 유저의 답글 삭제 (parent 삭제 전 먼저 처리)
    @Modifying
    @Query(value = "DELETE c1 FROM COMMENT c1 INNER JOIN COMMENT c2 ON c1.parent_id = c2.comment_id WHERE c2.user_id = :userId", nativeQuery = true)
    void deleteRepliesOfUserComments(@Param("userId") Long userId);

    //TODO: 유저 본인 댓글 + 유저 게시글에 달린 모든 댓글 삭제
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.user.userId = :userId OR c.runningLog.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
