package Termproject.Termproject2.domain.user.repository;

import Termproject.Termproject2.domain.user.dto.response.TermsResponseDto;
import Termproject.Termproject2.domain.user.entity.Terms;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserTermsAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TermsRepository extends JpaRepository<Terms, Long> {
    //TODO: 모든 약관 조회
    @Query("select new Termproject.Termproject2.domain.user.dto.response.TermsResponseDto(t.termsId , t.isRequired, t.title, t.content) from Terms t")
    List<TermsResponseDto> findAllTerms();

}

