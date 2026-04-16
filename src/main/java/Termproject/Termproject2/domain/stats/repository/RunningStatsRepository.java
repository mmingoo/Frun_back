package Termproject.Termproject2.domain.stats.repository;

import Termproject.Termproject2.domain.stats.entity.RunningStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RunningStatsRepository extends JpaRepository<RunningStats, Long> {
    Optional<RunningStats> findByUserUserIdAndStatTypeAndStatKey(
            Long userId, RunningStats.StatType statType, String statKey);

    @Modifying
    @Query("DELETE FROM RunningStats s WHERE s.user.userId = :userId")
    void deleteAllByUserUserId(@Param("userId") Long userId);
}
