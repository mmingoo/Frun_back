package Termproject.Termproject2.domain.running.repository;

import Termproject.Termproject2.domain.running.entity.Like;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Like, Long> {
    //TODO: 유저·러닝일지 ID로 좋아요 존재 여부 확인
    boolean existsByUserUserIdAndRunningLogRunningLogId(Long userId, Long runningLogId);
    //TODO: 유저·러닝일지 ID로 좋아요 삭제
    void deleteByUserUserIdAndRunningLogRunningLogId(Long userId, Long runningLogId);

    //TODO: 유저가 좋아요한 러닝일지 ID 목록 조회
    @Query("select l.runningLog.runningLogId from Like l where l.user.userId = :userId and l.runningLog.runningLogId In :logIds")
    Set<Long> findLikedLogIds(@Param("userId") Long userId , @Param("logIds") List<Long> logIds);

    //TODO: 유저와 관련된 모든 좋아요 삭제 (회원 탈퇴 시)
    @Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Like l WHERE l.user.userId = :userId OR l.runningLog.user.userId = :userId")
    void deleteAllByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}
