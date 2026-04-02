package Termproject.Termproject2.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 생성 요청 DTO
@Getter
public class CommentCreateRequest {
    @NotBlank
    private String content;
}