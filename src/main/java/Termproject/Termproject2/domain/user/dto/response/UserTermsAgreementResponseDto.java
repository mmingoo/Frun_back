package Termproject.Termproject2.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserTermsAgreementResponseDto {
    private Long agreementId;
    private String title;
    private Boolean isRequired;
    private Boolean isAgreed;
    private LocalDateTime agreedAt;
}