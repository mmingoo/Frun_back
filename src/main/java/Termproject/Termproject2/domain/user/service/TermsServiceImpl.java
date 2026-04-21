package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.converter.UserTermsAgreementConverter;
import Termproject.Termproject2.domain.user.dto.request.TermsAgreementRequest;
import Termproject.Termproject2.domain.user.dto.request.TermsUpdateRequest;
import Termproject.Termproject2.domain.user.dto.response.TermsResponseDto;
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


    //TODO: 약관 동의 저장 (최초 가입 시)
    @Override
    @Transactional
    public void saveAgreements(Long userId, TermsAgreementRequest request) {
        User user = userService.findUserById(userId);

        // 요청에서 동의한 약관 ID Set 추출 후 필수 약관 검증
        Set<Long> agreedIds = request.getAgreements().stream()
                .filter(TermsAgreementRequest.TermsAgreementItem::getIsAgreed) // 동의한 약관에 대해서
                .map(TermsAgreementRequest.TermsAgreementItem::getTermsId) // 약관 ID만추출
                .collect(Collectors.toSet()); // 중복 제거를 위해 Set 으로 데이터 저장

        // 필수 약관 전체 동의 여부 검증
        validateRequiredAgreements(agreedIds);

        // 동의한 약관이 존재하는 약관
        List<UserTermsAgreement> agreements = request.getAgreements().stream()
                .map(item -> {
                    // 약관 ID로 약관 엔티티 조회
                    Terms terms = termsRepository.findById(item.getTermsId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약관입니다."));

                    // 유저, 약관, 동의 여부로 UserTermsAgreement 엔티티 생성
                    return UserTermsAgreementConverter.toUserTermsAgreement(user, terms, item.getIsAgreed());
                })
                .collect(Collectors.toList());

        userTermsAgreementRepository.saveAll(agreements);
    }

    //TODO: 약관 동의 변경
    @Override
    @Transactional
    public void updateAgreements(Long userId, TermsUpdateRequest request) {

        // 요청에서 동의한 약관 ID Set 추출 후 필수 약관 검증
        Set<Long> agreedIds = request.getAgreements().stream()
                .filter(TermsUpdateRequest.TermsUpdateItem::getAgreed) // 동의한 약관에 대해서
                .map(TermsUpdateRequest.TermsUpdateItem::getTermId)// 약관 ID 추출만 추출
                .collect(Collectors.toSet()); // List todtjd

        // 필수 약관 전체 동의 여부 검증
        validateRequiredAgreements(agreedIds);

        for (TermsUpdateRequest.TermsUpdateItem item : request.getAgreements()) {

            // 유저 ID와 약관 ID로 기존 동의 항목 조회
            UserTermsAgreement agreement = userTermsAgreementRepository
                    .findByUserIdAndTermsId(userId, item.getTermId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.TERMS_AGREEMENT_NOT_FOUND));

            // 동의 여부를 유저가 요청 값으로 변경
            agreement.updateAgreement(item.getAgreed());
        }
    }

    //TODO: 전체 약관 목록 조회
    public List<TermsResponseDto> getTerms() {
        return termsRepository.findAllTerms();
    }

    // 필수 약관 동의 여부 검증
    // agreedIds: 사용자가 동의한 약관 ID Set
    private void validateRequiredAgreements(Set<Long> agreedIds) {
        // DB에서 필수 약관 목록 조회
        List<Terms> requiredTermsList = termsRepository.findAll().stream()
                .filter(Terms::getIsRequired)
                .toList();

        // 모든 필수 약관 ID가 사용자 동의 목록에 포함되어 있는지 확인
        boolean allRequiredAgreed = requiredTermsList.stream()
                .allMatch(terms -> agreedIds.contains(terms.getTermsId()));

        // 필수 약관 중 미동의 항목이 있으면 예외 발생
        if (!allRequiredAgreed) {
            throw new BusinessException(ErrorCode.TERM_NOT_COMPLETED);
        }
    }
}