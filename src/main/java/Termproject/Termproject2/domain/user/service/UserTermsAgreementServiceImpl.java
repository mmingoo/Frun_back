package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.response.UserTermsAgreementResponseDto;
import Termproject.Termproject2.domain.user.repository.UserTermsAgreementRepository;
import Termproject.Termproject2.domain.user.repository.UserTermsAgreementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTermsAgreementServiceImpl implements UserTermsAgreementService {

    private final UserTermsAgreementRepository userTermsAgreementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserTermsAgreementResponseDto> getMyTermsAgreements(Long userId) {
        return userTermsAgreementRepository.findAllByUserId(userId);
    }

}