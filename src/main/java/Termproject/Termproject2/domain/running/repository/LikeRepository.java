package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserUserIdAndRunningLogRunningLogId(Long userId, Long runningLogId);
    void deleteByUserUserIdAndRunningLogRunningLogId(Long userId, Long runningLogId);
}
