package Termproject.Termproject2.domain.comment.dto.response;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.user.entity.UserStatus;
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
    private String nickname; // 답글 작성자의 nickname
    private String profileImageUrl; // 답글 작성자의 프로필 사진 url
    private LocalDateTime createdAt;

    // 답글 dto 생성하는 빌더
    public static ReplyResponse from(Comment comment) {
        boolean isInactive = comment.getUser().getUserStatus().isInactive();
        return ReplyResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .userId(comment.getUser().getUserId())
                .nickname(isInactive ? "비활성화계정" : comment.getUser().getNickName())
                .profileImageUrl(isInactive ? null : comment.getUser().getImageUrl())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    //TODO: 프로필 이미지 URL 업데이트
    public void updateProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
}