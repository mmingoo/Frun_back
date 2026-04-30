package Termproject.Termproject2.domain.comment.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteCommentResponse {

    private final DeleteType deleteType;
    private final int deletedReplyCount;

    public enum DeleteType {
        CASCADE,           // 러닝일지 주인이 댓글 삭제 — 댓글 + 모든 답글 영구 삭제
        HARD,              // 영구 삭제 — 답글 없는 댓글 또는 답글 단건 삭제
        HARD_WITH_PARENT,  // 답글 삭제 후 소프트 삭제된 부모 댓글도 연쇄 영구 삭제
        SOFT               // 소프트 삭제 — 답글이 존재해 '삭제된 댓글입니다' 처리
    }

    public static DeleteCommentResponse cascade(int deletedReplyCount) {
        return new DeleteCommentResponse(DeleteType.CASCADE, deletedReplyCount);
    }

    public static DeleteCommentResponse hard() {
        return new DeleteCommentResponse(DeleteType.HARD, 0);
    }

    public static DeleteCommentResponse hardWithParent() {
        return new DeleteCommentResponse(DeleteType.HARD_WITH_PARENT, 0);
    }

    public static DeleteCommentResponse soft() {
        return new DeleteCommentResponse(DeleteType.SOFT, 0);
    }
}
