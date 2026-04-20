package Termproject.Termproject2.domain.user.repository;

import Termproject.Termproject2.domain.user.dto.response.UserTermsAgreementResponseDto;
import Termproject.Termproject2.domain.user.entity.UserTermsAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserTermsAgreementRepository extends JpaRepository<UserTermsAgreement, Long> {

    //TODO: 유저의 약관 동의 목록 DTO 조회
    @Query("SELECT new Termproject.Termproject2.domain.user.dto.response.UserTermsAgreementResponseDto" +
           "(uta.agreementId, t.title, t.isRequired, uta.isAgreed, uta.createdAt) " +
           "FROM UserTermsAgreement uta " +
           "JOIN uta.terms t " +
           "WHERE uta.user.userId = :userId")
    List<UserTermsAgreementResponseDto> findAllByUserId(@Param("userId") Long userId);

    //TODO: 유저·약관 ID로 동의 항목 조회
    @Query("SELECT uta FROM UserTermsAgreement uta WHERE uta.user.userId = :userId AND uta.terms.termsId = :termsId")
    java.util.Optional<UserTermsAgreement> findByUserIdAndTermsId(@Param("userId") Long userId, @Param("termsId") Long termsId);
}