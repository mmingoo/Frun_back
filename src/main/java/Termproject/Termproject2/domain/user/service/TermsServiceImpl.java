package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.request.TermsAgreementRequest;
import Termproject.Termproject2.domain.user.dto.response.TermsResponse;
import Termproject.Termproject2.domain.user.entity.Terms;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserTermsAgreement;
import Termproject.Termproject2.domain.user.repository.TermsRepository;
import Termproject.Termproject2.domain.user.repository.UserTermsAgreementRepository;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsServiceImpl implements TermsService {

    private final TermsRepository termsRepository;
    private final UserTermsAgreementRepository userTermsAgreementRepository;
    private final UserService userService;


    @Override
    @Transactional
    public void saveAgreements(Long userId, TermsAgreementRequest request) {

        User user = userService.findById(userId);
        validateRequiredAgreements(request);

        List<UserTermsAgreement> agreements = request.getAgreements().stream()
                .map(item -> {
                    Terms terms = termsRepository.findById(item.getTermsId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약관입니다."));
                    return UserTermsAgreement.of(user, terms, item.getIsAgreed());
                })
                .collect(Collectors.toList());

        userTermsAgreementRepository.saveAll(agreements);
    }


    private void validateRequiredAgreements(TermsAgreementRequest request) {
        // 필수 약관 전체 조회
        List<Terms> requiredTermsList = termsRepository.findAll().stream()
                .filter(Terms::getIsRequired)
                .collect(Collectors.toList());

        // 요청에서 필수 약관 동의 여부 확인
        Set<Long> agreedRequiredIds = request.getAgreements().stream()
                .filter(TermsAgreementRequest.TermsAgreementItem::getIsAgreed)
                .map(TermsAgreementRequest.TermsAgreementItem::getTermsId)
                .collect(Collectors.toSet());

        boolean allRequiredAgreed = requiredTermsList.stream()
                .allMatch(terms -> agreedRequiredIds.contains(terms.getTermsId()));

        if (!allRequiredAgreed) {
            throw new BusinessException(ErrorCode.TERM_NOT_COMPLETED);
        }
    }
}