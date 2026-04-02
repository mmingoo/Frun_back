package Termproject.Termproject2.domain.comment.dto.response;

import Termproject.Termproject2.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 답글 응답 DTO
@Getter
@Builder
public class ReplyResponse {
    private Long commentId;
    private String content;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdAt;

    public static ReplyResponse from(Comment comment) {
        return ReplyResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .userId(comment.getUser().getUserId())
                .nickname(comment.getUser().getNickName())
                .profileImageUrl(comment.getUser().getImageUrl())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}