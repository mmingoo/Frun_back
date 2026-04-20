package Termproject.Termproject2.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
// 유저 약관 동의 현황 응답
public class UserTermsAgreementResponseDto {
    private Long agreementId; // 동의 ID
    private String title; // 약관 제목
    private Boolean isRequired; // 필수 약관 여부
    private Boolean isAgreed; // 동의 여부
    private LocalDateTime agreedAt; // 동의 일시
}