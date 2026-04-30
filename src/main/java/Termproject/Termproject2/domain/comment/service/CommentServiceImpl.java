package Termproject.Termproject2.domain.comment.service;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.comment.converter.CommentConverter;
import Termproject.Termproject2.domain.comment.dto.request.CommentCreateRequest;
import Termproject.Termproject2.domain.comment.dto.response.CommentResponse;
import Termproject.Termproject2.domain.comment.dto.response.CursorSliceResponse;
import Termproject.Termproject2.domain.comment.dto.response.DeleteCommentResponse;
import Termproject.Termproject2.domain.comment.dto.response.ReplyResponse;
import Termproject.Termproject2.domain.comment.repository.CommentRepository;
import Termproject.Termproject2.domain.notification.service.NotificationService;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.service.RunningLogService;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.service.UserService;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final RunningLogService runningLogService;
    private final UserService userService;
    private final ImageService imageService;
    private final NotificationService notificationService;


    //TODO: 댓글 목록 조회
    @Override
    public CursorSliceResponse<CommentResponse> getComment(Long runningLogId, Long cursorId, int size) {

        // 존재하는 러닝일지인지 검증
        validateRunningLogExists(runningLogId);
        List<Comment> comments =  commentRepository.findTopLevelComments(runningLogId, cursorId, size);

        // 무한스크롤 할 다음이 있는지 판단
        boolean hasNext = getHasNext(comments, size);

        // 마지막 댓글 제거 (불러올 때 size + 1 개 만큼 데이터를 가져왔으므로 마지막 데이터 삭제)
        if(hasNext) comments.remove(size);


        // 답글 수 일괄 집계를 위한 댓글 ID 목록
        List<Long> commentIds = comments.stream()
                .map(Comment::getCommentId)
                .toList();

        // 각 댓글이 가지고 있는 답글의 개수 조회
        //  <commentId, replyCount>
        Map<Long , Long> replyCount = commentRepository.countRepliesByParentIds(commentIds);

        // dto 에 댓글마다 답글 갯수를 세팅
        List<CommentResponse> contents = comments.stream()
                .map(c -> CommentResponse.of(c, replyCount.getOrDefault(c.getCommentId(),0L)))
                .toList();

        // 프로필 이미지명을 full url 로 변환
        toCommentFullCProfileImageUrls(contents);

        // size + 1 개 댓글을 불러왔고, 이 중 가장 마지막 댓글의 Id가 cursorId
        Long nextCursorId = getNextCursorId(comments, hasNext);

        // 해당 러닝일지의 전체 최상위 댓글 수
        long totalCount = commentRepository.countByRunningLogRunningLogIdAndParentIsNull(runningLogId);

        return new CursorSliceResponse<>(contents, hasNext, nextCursorId, totalCount);
    }




    //TODO: 답글 목록 조회
    @Override
    public CursorSliceResponse<ReplyResponse> getReplies(Long parentId, Long cursorId, int size) {
        // 답글 가져오기
        List<Comment> replies = commentRepository.findReplies(parentId,cursorId, size);

        // 다음 스크롤 있는지 여부
        boolean hasNext = getHasNext(replies, size);

        // 다음 스크롤 있다면 size + 1 개에서 마지막 제거
        if(hasNext) replies.remove(size);
        List<ReplyResponse> contents = replies.stream()
                .map(ReplyResponse::from)
                .toList();

        // size + 1 개 댓글을 불러왔고, 이 중 가장 마지막 댓글의 Id가 cursorId
        Long nextCursorId = getNextCursorId(replies, hasNext);

        // 답글마다 프로필 사진 가져오기
        toReplyFullProfileImageUrls(contents);

        // 해당 댓글의 전체 답글 수
        long totalCount = commentRepository.countByParentCommentId(parentId);

        return new CursorSliceResponse<>(contents, hasNext, nextCursorId, totalCount);
    }

    //TODO: 댓글 작성
    @Transactional
    @Override
    public Long createComment(Long runningLogId, Long userId, CommentCreateRequest request) {

        //러닝로그 조회
        RunningLog runningLog = findRunningLogById(runningLogId);

        // 유저 조회
        User user = findUserById(userId);

        // 댓글 생성
        Comment comment = CommentConverter.toComment(runningLog, user, request.getContent());

        // 댓글 저장
        Comment saved = commentRepository.save(comment);

        // 작성자 가져오기
        User logAuthor = runningLog.getUser();

        // 댓글과 작성자에 대한 알림 생성
        notificationService.notifyComment(logAuthor, saved);

        return saved.getCommentId();

    }

    //TODO: 답글 작성
    @Transactional
    @Override
    public Long createReply(Long runningLogId, Long userId, Long parentId, CommentCreateRequest request) {

        // 러닝일지 조회
        RunningLog runningLog = findRunningLogById(runningLogId);

        // 댓글 작성자 조회
        User user = findUserById(userId);

        // 부모 댓글 조회
        Comment parent = findCommentById(parentId);

        // depth 2 초과 방지 , 부모 댓글의 부모가 있으면 답글 불가
        if (parent.getParent() != null) {
            throw new BusinessException(ErrorCode.EXCEEDED_COMMENT_DEPTH);
        }

        // 부모 댓글이 해당 게시물 소속인지 검증
        if (!parent.getRunningLog().getRunningLogId().equals(runningLogId)) {
            throw new BusinessException(ErrorCode.INVALID_COMMENT_PARENT);
        }

        // 답글 생성
        Comment reply = CommentConverter.toReply(runningLog, user, request.getContent(), parent);

        // 답글 저장
        Comment saved = commentRepository.save(reply);

        // 부모 댓글 작성자에게 답글 알림 전송 (본인 댓글에 본인 답글이면 내부에서 필터링)
        User parentCommentAuthor = parent.getUser();

        // 답글 알림 생성
        notificationService.notifyComment(parentCommentAuthor, saved);

        return saved.getCommentId();
    }

    //TODO: 댓글/답글 수정
    @Transactional
    @Override
    public void updateComment(Long commentId, Long userId, CommentCreateRequest request) {
        // 댓글 조회
        Comment comment = findCommentById(commentId);

        // 댓글 작성자인지 검토
        validateOwner(comment, userId);

        // 댓글 검토
        comment.update(request.getContent());
    }

    //TODO: 댓글/답글 삭제
    @Transactional
    @Override
    public DeleteCommentResponse deleteComment(Long commentId, Long userId) {

        Comment comment = findCommentById(commentId);

        // 댓글 삭제 권한 검증
        validateDeletePermission(comment, userId);

        boolean isLogOwner = comment.getRunningLog().getUser().getUserId().equals(userId);

        if (isLogOwner) {
            // 러닝일지 주인이 삭제: 답글 포함 전체 실제 삭제
            return deleteWithReplies(comment);
        } else {
            // 댓글 작성자가 삭제: 소프트 삭제 로직
            return deleteAsCommentOwner(comment);
        }
    }

    // 러닝일지 주인의 삭제: 답글 + 댓글 모두 실제 삭제
    private DeleteCommentResponse deleteWithReplies(Comment comment) {
        List<Comment> children = comment.getChildren();
        int deletedReplyCount = children.size();

        // 답글 알림 + 댓글 알림 일괄 삭제
        List<Comment> all = new ArrayList<>(children);
        all.add(comment);
        notificationService.deleteByComments(all);

        // 답글 먼저 삭제
        if (!children.isEmpty()) {
            commentRepository.deleteAll(children);
            commentRepository.flush();
        }

        // 부모 댓글 참조는 삭제 전에 캡처
        Comment parent = comment.getParent();

        // 댓글(또는 답글) 삭제
        commentRepository.delete(comment);
        commentRepository.flush();

        // 답글 삭제 후 부모가 소프트 삭제 상태이고 남은 자식이 없으면 부모도 영구 삭제
        if (parent != null && parent.isDeleted()
                && commentRepository.countByParentCommentId(parent.getCommentId()) == 0) {
            commentRepository.delete(parent);
            return DeleteCommentResponse.hardWithParent();
        }

        return deletedReplyCount > 0
                ? DeleteCommentResponse.cascade(deletedReplyCount)
                : DeleteCommentResponse.hard();
    }

    // 댓글 작성자의 삭제: 답글 유무에 따라 소프트/실제 삭제
    private DeleteCommentResponse deleteAsCommentOwner(Comment comment) {
        notificationService.deleteByComments(List.of(comment));

        boolean hasChildren = commentRepository.countByParentCommentId(comment.getCommentId()) > 0;

        if (hasChildren) {
            // 답글이 달린 부모 댓글: 소프트 삭제 (답글은 유지)
            comment.softDelete();
            return DeleteCommentResponse.soft();
        } else {
            // 답글 없는 댓글 또는 답글 자체: 실제 삭제
            Comment parent = comment.getParent();
            commentRepository.delete(comment);
            commentRepository.flush();

            // 답글 삭제 후 부모가 소프트 삭제 상태이고 자식이 없으면 부모도 실제 삭제
            if (parent != null && parent.isDeleted()
                    && commentRepository.countByParentCommentId(parent.getCommentId()) == 0) {
                commentRepository.delete(parent);
                return DeleteCommentResponse.hardWithParent();
            }

            return DeleteCommentResponse.hard();
        }
    }


    //commentId로 댓글 조회
    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(()-> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
    }

    //runningLogId로 러닝일지 조회
    private RunningLog findRunningLogById(Long runningLogId) {
        return runningLogService.findById(runningLogId);
    }

    //userId로 유저 조회
    private User findUserById(Long userId) {
        return userService.findUserById(userId);
    }

    //러닝일지 존재 여부 검증
    private void validateRunningLogExists(Long runningLogId) {
        if (!runningLogService.existsById(runningLogId)) {
            throw new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND);
        }
    }

    //댓글 작성자 검증
    private void validateOwner(Comment comment, Long userId) {
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new BusinessException( ErrorCode.NOT_COMMENT_OWNER);
        }
    }


    //댓글 삭제 권한 검증
    private void validateDeletePermission(Comment comment, Long userId) {
        boolean isCommentOwner = comment.getUser().getUserId().equals(userId);
        boolean isLogOwner = comment.getRunningLog().getUser().getUserId().equals(userId);

        // 댓글 작성자 혹은 게시글 주인이 아니면 삭제 불가능
        if (!isCommentOwner && !isLogOwner) {
            throw new BusinessException(ErrorCode.NOT_COMMENT_OWNER);
        }
    }

    //다음 페이지 존재 여부 판단
    private boolean getHasNext(List<Comment> comments, int size){
        return comments.size() > size;
    }

    //다음 커서 ID 계산
    private Long getNextCursorId(List<Comment> contents, boolean hasNext) {
        return hasNext
                ? contents.get(contents.size() -1).getCommentId() // hasNext 가 true인 경우
                :null;

    }

    // 댓글마다 댓글 작성자 프로필 이미지 설정
    private void toCommentFullCProfileImageUrls(List<CommentResponse> contents) {
        contents.forEach(c -> c.updateProfileImageUrl(
                imageService.getProfileImageUrl(c.getProfileImageUrl())));
    }

    // 답글마다 프로필 이미지 설정
    private void toReplyFullProfileImageUrls(List<ReplyResponse> contents) {
        contents.forEach(c -> c.updateProfileImageUrl(
                imageService.getProfileImageUrl(c.getProfileImageUrl())));
    }

}
