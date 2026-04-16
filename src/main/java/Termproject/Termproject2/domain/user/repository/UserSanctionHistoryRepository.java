package Termproject.Termproject2.domain.user.repository;

import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserSanctionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserSanctionHistoryRepository extends JpaRepository<UserSanctionHistory, Long> {

    Optional<UserSanctionHistory> findTopByTargetUserOrderByCreatedAtDesc(User targetUser);

    @Modifying
    @Query("DELETE FROM UserSanctionHistory h WHERE h.targetUser.userId = :userId")
    void deleteAllByTargetUserUserId(@Param("userId") Long userId);
}
