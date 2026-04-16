package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.entity.RunningLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface RunningLogRepository extends JpaRepository<RunningLog, Long>, RunningLogRepositoryCustom {
    List<RunningLog> findByUserUserIdAndIsDeletedFalseAndRunDateBetween(Long userId, LocalDate start, LocalDate end);
    Optional<RunningLog> findByRunningLogIdAndIsDeletedFalse(Long runningLogId);

    // 게시물 수, 총 거리,평균 페이스 집계
    @Query(value = "SELECT COUNT(*), COALESCE(SUM(distance), 0.0), COALESCE(SUM(TIME_TO_SEC(duration)), 0) " +
                   "FROM RUNNING_LOG WHERE user_id = :userId AND is_deleted = false AND  is_public = true",
           nativeQuery = true)
    List<Object[]> aggregateStatsByUserId(@Param("userId") Long userId);

    List<RunningLog> findAllByIsDeletedTrueAndDeletionScheduledAtBefore(LocalDateTime now);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM RunningLog rl WHERE rl.user.userId = :userId")
    void deleteAllByUserUserId(@Param("userId") Long userId);
}
