package Termproject.Termproject2.domain.user.repository;

import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserSanctionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSanctionHistoryRepository extends JpaRepository<UserSanctionHistory, Long> {

    Optional<UserSanctionHistory> findTopByTargetUserOrderByCreatedAtDesc(User targetUser);
}
