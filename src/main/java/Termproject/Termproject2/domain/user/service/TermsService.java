package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.request.TermsAgreementRequest;
import Termproject.Termproject2.domain.user.dto.request.TermsUpdateRequest;
import Termproject.Termproject2.domain.user.dto.response.TermsResponseDto;

import java.util.List;

public interface TermsService {
    //TODO: 약관 동의 저장 (회원가입 시)
    void saveAgreements(Long userId, TermsAgreementRequest request);

    //TODO: 약관 동의 변경
    void updateAgreements(Long userId, TermsUpdateRequest request);

    //TODO: 전체 약관 목록 조회
    List<TermsResponseDto> getTerms();
}