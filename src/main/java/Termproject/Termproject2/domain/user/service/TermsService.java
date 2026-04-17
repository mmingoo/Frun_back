package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.request.TermsAgreementRequest;
import Termproject.Termproject2.domain.user.dto.request.TermsUpdateRequest;
import Termproject.Termproject2.domain.user.dto.response.TermsResponseDto;

import java.util.List;

public interface TermsService {
    void saveAgreements(Long userId, TermsAgreementRequest request);
    void updateAgreements(Long userId, TermsUpdateRequest request);
    List<TermsResponseDto> getTerms();
}