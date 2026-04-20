package Termproject.Termproject2.domain.user.dto.request;

import Termproject.Termproject2.domain.user.entity.Terms;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 약관 동의 저장 요청 DTO (회원가입 시)
@Getter
@NoArgsConstructor
public class TermsAgreementRequest {
    private List<TermsAgreementItem> agreements; // 약관별 동의 여부 목록

    @Getter
    @NoArgsConstructor
    public static class TermsAgreementItem {
        private Long termsId; // 약관 ID
        private Boolean isAgreed; // 동의 여부
    }
}

