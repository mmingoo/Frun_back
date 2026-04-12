package Termproject.Termproject2.global.scheduler;

import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletionScheduler {

    private final UserRepository userRepository;
    private final RunningLogRepository runningLogRepository;
    private final EntityManager em;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteExpiredUsers() {
        // INACTIVE / DIRECT_INACTIVE / REPORT_INACTIVE 세 상태 모두 삭제 대상으로 처리
        List<User> targets = userRepository.findAllByUserStatusInAndDeletionScheduledAtBefore(
                List.of(UserStatus.INACTIVE, UserStatus.DIRECT_INACTIVE, UserStatus.REPORT_INACTIVE),
                LocalDateTime.now());

        if (targets.isEmpty()) return;

        for (User user : targets) {
            Long userId = user.getUserId();
            log.info("만료 계정 삭제 시작: userId={}", userId);

            // 1. 다른 유저의 알림 중 이 유저의 러닝로그/댓글을 참조하는 것 삭제
            em.createQuery("DELETE FROM Notification n WHERE n.runningLog.runningLogId IN (SELECT rl.runningLogId FROM RunningLog rl WHERE rl.user.userId = :userId)")
                    .setParameter("userId", userId).executeUpdate();
            em.createQuery("DELETE FROM Notification n WHERE n.comment IN (SELECT c FROM Comment c WHERE c.runningLog.user.userId = :userId)")
                    .setParameter("userId", userId).executeUpdate();

            // 2. 이 유저가 받거나 보낸 알림 삭제
            em.createQuery("DELETE FROM Notification n WHERE n.user.userId = :userId OR n.sender.userId = :userId")
                    .setParameter("userId", userId).executeUpdate();

            // 3. 친구 요청 삭제
            em.createQuery("DELETE FROM FriendRequest fr WHERE fr.receiver.userId = :userId OR fr.sender.userId = :userId")
                    .setParameter("userId", userId).executeUpdate();

            // 4. 친구 관계 삭제
            em.createQuery("DELETE FROM Friendship f WHERE f.id.receiveUserId = :userId OR f.id.senderUserId = :userId")
                    .setParameter("userId", userId).executeUpdate();

            // 5. 이 유저의 러닝로그에 달린 좋아요 삭제
            em.createQuery("DELETE FROM Like l WHERE l.runningLog.runningLogId IN (SELECT rl.runningLogId FROM RunningLog rl WHERE rl.user.userId = :userId)")
                    .setParameter("userId", userId).executeUpdate();

            // 6. 이 유저가 누른 좋아요 삭제
            em.createQuery("DELETE FROM Like l WHERE l.user.userId = :userId")
                    .setParameter("userId", userId).executeUpdate();

            // 7. 이 유저의 러닝로그에 달린 대댓글 → 루트 댓글 순 삭제
            em.createQuery("DELETE FROM Comment c WHERE c.parent IS NOT NULL AND c.runningLog.user.userId = :userId")
                    .setParameter("userId", userId).executeUpdate();
            em.createQuery("DELETE FROM Comment c WHERE c.parent IS NULL AND c.runningLog.user.userId = :userId")
                    .setParameter("userId", userId).executeUpdate();

            // 8. 이 유저가 다른 로그에 작성한 대댓글 → 루트 댓글 순 삭제
            em.createQuery("DELETE FROM Comment c WHERE c.parent IS NOT NULL AND c.user.userId = :userId")
                    .setParameter("userId", userId).executeUpdate();
            em.createQuery("DELETE FROM Comment c WHERE c.parent IS NULL AND c.user.userId = :userId")
                    .setParameter("userId", userId).executeUpdate();

            // 9. 러닝로그 이미지 삭제 (JPQL 벌크 삭제 시 cascade 미작동)
            em.createQuery("DELETE FROM RunningLogImage ri WHERE ri.runningLog.runningLogId IN (SELECT rl.runningLogId FROM RunningLog rl WHERE rl.user.userId = :userId)")
                    .setParameter("userId", userId).executeUpdate();

            // 10. 러닝로그 삭제
            em.createQuery("DELETE FROM RunningLog rl WHERE rl.user.userId = :userId")
                    .setParameter("userId", userId).executeUpdate();

            // 11. 유저 삭제
            userRepository.delete(user);

            log.info("만료 계정 삭제 완료: userId={}", userId);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteExpiredRunningLogs() {
        List<RunningLog> targets = runningLogRepository.findAllByIsDeletedTrueAndDeletionScheduledAtBefore(
                LocalDateTime.now());

        if (targets.isEmpty()) return;

        for (RunningLog runningLog : targets) {
            Long logId = runningLog.getRunningLogId();
            log.info("만료 러닝일지 삭제 시작: runningLogId={}", logId);

            // 1. 해당 러닝로그를 참조하는 알림 삭제
            em.createQuery("DELETE FROM Notification n WHERE n.runningLog.runningLogId = :logId")
                    .setParameter("logId", logId).executeUpdate();

            // 2. 해당 러닝로그 댓글을 참조하는 알림 삭제
            em.createQuery("DELETE FROM Notification n WHERE n.comment IN (SELECT c FROM Comment c WHERE c.runningLog.runningLogId = :logId)")
                    .setParameter("logId", logId).executeUpdate();

            // 3. 좋아요 삭제
            em.createQuery("DELETE FROM Like l WHERE l.runningLog.runningLogId = :logId")
                    .setParameter("logId", logId).executeUpdate();

            // 4. 대댓글 → 루트 댓글 순 삭제
            em.createQuery("DELETE FROM Comment c WHERE c.parent IS NOT NULL AND c.runningLog.runningLogId = :logId")
                    .setParameter("logId", logId).executeUpdate();
            em.createQuery("DELETE FROM Comment c WHERE c.parent IS NULL AND c.runningLog.runningLogId = :logId")
                    .setParameter("logId", logId).executeUpdate();

            // 5. 이미지 삭제
            em.createQuery("DELETE FROM RunningLogImage ri WHERE ri.runningLog.runningLogId = :logId")
                    .setParameter("logId", logId).executeUpdate();

            // 6. 러닝로그 삭제
            runningLogRepository.delete(runningLog);

            log.info("만료 러닝일지 삭제 완료: runningLogId={}", logId);
        }
    }
}
