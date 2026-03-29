package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.entity.RunningLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RunningLogRepository extends JpaRepository<RunningLog, Long>, RunningLogRepositoryCustom {
    List<RunningLog> findByUserUserIdAndIsDeletedFalseAndRunDateBetween(Long userId, LocalDate start, LocalDate end);
    Optional<RunningLog> findByRunningLogIdAndIsDeletedFalse(Long runningLogId);

}
