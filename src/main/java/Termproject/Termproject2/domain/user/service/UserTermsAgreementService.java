package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.response.UserTermsAgreementResponseDto;

import java.util.List;

public interface UserTermsAgreementService {
    //TODO: 내가 동의한 약관 조회
    List<UserTermsAgreementResponseDto> getMyTermsAgreements(Long userId);
}