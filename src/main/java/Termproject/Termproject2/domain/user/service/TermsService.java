package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.request.TermsAgreementRequest;
import Termproject.Termproject2.domain.user.dto.response.TermsResponse;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.repository.UserTermsAgreementRepository;

import java.util.List;

public interface TermsService {
    void saveAgreements(Long userId, TermsAgreementRequest request);

}