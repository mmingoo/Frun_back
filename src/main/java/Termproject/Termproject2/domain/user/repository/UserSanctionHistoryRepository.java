package Termproject.Termproject2.domain.user.repository;

import Termproject.Termproject2.domain.user.entity.SanctionType;
import Termproject.Termproject2.domain.user.entity.UserSanctionHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSanctionHistoryRepository extends JpaRepository<UserSanctionHistory, Long> {

    // 관리자 직접 제재 이력 중 가장 최근 항목 조회
    @Query("SELECT h FROM UserSanctionHistory h WHERE h.targetUser.userId = :userId AND h.sanctionType = :sanctionType ORDER BY h.createdAt DESC limit 1")
    List<UserSanctionHistory> findLatestSanction(@Param("userId") Long userId, @Param("sanctionType") SanctionType sanctionType);


}
