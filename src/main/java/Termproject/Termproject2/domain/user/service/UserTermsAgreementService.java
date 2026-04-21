package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.response.UserTermsAgreementResponseDto;

import java.util.List;

public interface UserTermsAgreementService {
    List<UserTermsAgreementResponseDto> getMyTermsAgreements(Long userId);
}