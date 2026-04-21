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
    //TODO: 특정 유저의 기간 내 공개·미삭제 러닝일지 조회 (통계용)
    List<RunningLog> findByUserUserIdAndIsDeletedFalseAndIsPublicTrueAndRunDateBetween(Long userId, LocalDate start, LocalDate end);

    //TODO: 삭제되지 않은 러닝일지 조회
    Optional<RunningLog> findByRunningLogIdAndIsDeletedFalse(Long runningLogId);

    //TODO: 유저 페이지용 러닝 통계 집계 (게시물 수, 총 거리, 총 시간)
    // Native 사용 이유: TIME_TO_SEC()은 MySQL 전용 함수로 JPQL에서 지원하지 않음
    @Query(value = "SELECT COUNT(*), COALESCE(SUM(distance), 0.0), COALESCE(SUM(TIME_TO_SEC(duration)), 0) " +
                   "FROM RUNNING_LOG WHERE user_id = :userId AND is_deleted = false AND  is_public = true",
           nativeQuery = true)
    List<Object[]> aggregateStatsByUserId(@Param("userId") Long userId);

    //TODO: soft 삭제되고 삭제 예정일이 지난 러닝일지 조회 (스케줄러용)
    List<RunningLog> findAllByIsDeletedTrueAndDeletionScheduledAtBefore(LocalDateTime now);

    //TODO: 특정 유저의 모든 러닝일지 물리 삭제
    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM RunningLog rl WHERE rl.user.userId = :userId")
    void deleteAllByUserUserId(@Param("userId") Long userId);
}
