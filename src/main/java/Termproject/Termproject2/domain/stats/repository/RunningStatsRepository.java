package Termproject.Termproject2.domain.stats.repository;

import Termproject.Termproject2.domain.stats.entity.RunningStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RunningStatsRepository extends JpaRepository<RunningStats, Long> {
    Optional<RunningStats> findByUserUserIdAndStatTypeAndStatKey(
            Long userId, RunningStats.StatType statType, String statKey);
}
