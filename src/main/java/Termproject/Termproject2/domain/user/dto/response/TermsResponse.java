package Termproject.Termproject2.domain.user.dto.response;

import Termproject.Termproject2.domain.user.entity.Terms;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 응답 - 약관 목록 조회 시
@Getter
@AllArgsConstructor
public class TermsResponse {
    private Long termsId;
    private String termsType;
    private String title;
    private Boolean isRequired;

    public static TermsResponse from(Terms terms) {
        return new TermsResponse(
            terms.getTermsId(),
            terms.getTermsType(),
            terms.getTitle(),
            terms.getIsRequired()
        );
    }
}