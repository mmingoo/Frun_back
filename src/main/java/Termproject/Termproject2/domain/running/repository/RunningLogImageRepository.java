package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.entity.RunningLogImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RunningLogImageRepository extends JpaRepository<RunningLogImage, Long> {

    @Modifying
    @Query("DELETE FROM RunningLogImage rli WHERE rli.runningLog.user.userId = :userId")
    void deleteAllByRunningLogUserUserId(@Param("userId") Long userId);
}
