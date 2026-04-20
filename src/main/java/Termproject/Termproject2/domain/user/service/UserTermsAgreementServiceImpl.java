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
@Transactional(readOnly = true)
public class UserTermsAgreementServiceImpl implements UserTermsAgreementService {

    private final UserTermsAgreementRepository userTermsAgreementRepository;

    //TODO: 내 약관 동의 목록 조회
    @Override
    public List<UserTermsAgreementResponseDto> getMyTermsAgreements(Long userId) {
        return userTermsAgreementRepository.findAllByUserId(userId);
    }

}