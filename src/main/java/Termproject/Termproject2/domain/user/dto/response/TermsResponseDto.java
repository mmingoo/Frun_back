package Termproject.Termproject2.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TermsResponseDto {
    private Long termId;
    private boolean isRequired;
    private String title;
    private String content;

}
