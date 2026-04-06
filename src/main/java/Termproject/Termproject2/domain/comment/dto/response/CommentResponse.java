package Termproject.Termproject2.domain.comment.dto.response;

import Termproject.Termproject2.domain.comment.Comment;
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
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .userId(comment.getUser().getUserId())
                .nickname(comment.getUser().getNickName())
                .profileImageUrl(comment.getUser().getImageUrl())
                .replyCount(replyCount)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    //프로필 이미지 경로 추가를 위핸 내부 메서드
    public void updateProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    };
}