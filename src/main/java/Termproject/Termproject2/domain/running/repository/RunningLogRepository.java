package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.entity.RunningLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunningLogRepository extends JpaRepository<RunningLog, Long> , RunningLogRepositoryCustom {
}
