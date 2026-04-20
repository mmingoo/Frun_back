package Termproject.Termproject2.domain.comment.dto.response;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
// 댓글 응답 DTO
@Getter
@Builder
public class CommentResponse {

    private Long commentId;
    private String content; // 댓글 내용
    private Long userId; // 작성자id
    private String nickname; // 닉네임
    private String profileImageUrl;
    private long replyCount; // 더보기 버튼 표시 여부 판단용
    private LocalDateTime createdAt; // 생성일자.

    // CommentResponse 생성하는 빌더
    public static CommentResponse of(Comment comment, long replyCount) {
        boolean isInactive = comment.getUser().getUserStatus().isInactive();
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .userId(comment.getUser().getUserId())
                .nickname(isInactive ? "비활성화 계정" : comment.getUser().getNickName())
                .profileImageUrl(isInactive ? null : comment.getUser().getImageUrl())
                .replyCount(replyCount)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    //TODO: 프로필 이미지 URL 업데이트
    public void updateProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    };
}