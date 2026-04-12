package Termproject.Termproject2.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 댓글 생성 요청 DTO
@Getter
public class CommentCreateRequest {
    @NotBlank
    @Size(max = 200, message = "댓글/답글은 최대 200자 까지 작성할 수 있습니다.")
    private String content; // 댓글/답글 내용
}