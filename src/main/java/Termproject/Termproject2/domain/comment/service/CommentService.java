package Termproject.Termproject2.domain.comment.service;


import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.comment.dto.request.CommentCreateRequest;
import Termproject.Termproject2.domain.comment.dto.response.CommentResponse;
import Termproject.Termproject2.domain.comment.dto.response.CursorSliceResponse;
import Termproject.Termproject2.domain.comment.dto.response.ReplyResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentService {
    //TODO: 댓글 목록 조회
    CursorSliceResponse<CommentResponse> getComment(Long runningId, Long cursorId, int size);

    //TODO: 답글 목록 조회
    CursorSliceResponse<ReplyResponse> getReplies(Long parentId, Long cursorId, int size);

    //TODO: 댓글 작성
    @Transactional
    Long createComment(Long runningLogId, Long userId, CommentCreateRequest request);

    //TODO: 답글 작성
    @Transactional
    Long createReply(Long runningLogId, Long parentId, Long userId, CommentCreateRequest request);

    //TODO: 댓글/답글 수정
    @Transactional
    void updateComment(Long commentId, Long userId, CommentCreateRequest request);

    //TODO: 댓글/답글 삭제
    @Transactional
    void deleteComment(Long commentId, Long userId);



}
