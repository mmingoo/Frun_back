package Termproject.Termproject2.domain.user.repository;

import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserTermsAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserTermsAgreementRepository extends JpaRepository<UserTermsAgreement, Long> {

    @Modifying
    @Query("DELETE FROM UserTermsAgreement uta WHERE uta.user.userId = :userId")
    void deleteAllByUserUserId(@Param("userId") Long userId);
}