package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.entity.Like;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserUserIdAndRunningLogRunningLogId(Long userId, Long runningLogId);
    void deleteByUserUserIdAndRunningLogRunningLogId(Long userId, Long runningLogId);

    @Query("select l.runningLog.runningLogId from Like l where l.user.userId = :userId and l.runningLog.runningLogId In :logIds")
    Set<Long> findLikedLogIds(@Param("userId") Long userId , @Param("logIds") List<Long> logIds);

}
