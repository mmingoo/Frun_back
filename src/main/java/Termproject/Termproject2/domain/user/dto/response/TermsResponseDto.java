package Termproject.Termproject2.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 약관 단건 응답
public class TermsResponseDto {
    private Long termId; // 약관 ID
    private boolean isRequired; // 필수 여부
    private String title; // 약관 제목
    private String content; // 약관 내용

}
