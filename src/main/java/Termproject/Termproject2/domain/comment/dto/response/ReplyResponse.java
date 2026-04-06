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
    private String content; // 답글 내용
    private Long userId; // 답글 작성자 id
    private String nickname; // 답글 작성자의 nickname
    private String profileImageUrl; // 답글 작성자의 프로필 사진 url
    private LocalDateTime createdAt;

    // 답글 dto 생성하는 빌더
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

    //프로필 이미지 경로 추가를 위핸 내부 메서드
    public void updateProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
}