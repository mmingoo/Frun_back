package Termproject.Termproject2.domain.user.dto.request;

import Termproject.Termproject2.domain.user.entity.Terms;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 요청 - 프론트에서 약관별 동의 여부 리스트로 받음
@Getter
@NoArgsConstructor
public class TermsAgreementRequest {
    private List<TermsAgreementItem> agreements;

    @Getter
    @NoArgsConstructor
    public static class TermsAgreementItem {
        private Long termsId;
        private Boolean isAgreed;
    }

}

