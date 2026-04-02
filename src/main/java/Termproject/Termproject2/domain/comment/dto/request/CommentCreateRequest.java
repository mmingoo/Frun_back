package Termproject.Termproject2.domain.comment.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 생성 요청 DTO
@Getter
public class CommentCreateRequest {
    @NotBlank
    @Max(value = 200, message ="댓글/답글은 최대 200자 까지 작성할 수 있습니다." )
    private String content;
}