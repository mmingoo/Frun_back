package Termproject.Termproject2.domain.report.repository;

import Termproject.Termproject2.domain.report.entity.Report;
import Termproject.Termproject2.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    //TODO: 동일한 신고자·대상 조합의 중복 신고 여부 확인
    boolean existsByReporterAndReportedUser(User reporter, User reportedUser);

    //TODO: 상태별 신고 목록 커서 기반 조회 (관리자용)
    @Query("""
            SELECT r FROM Report r
            WHERE (:status IS NULL OR r.status = :status)
              AND (:lastReportId IS NULL OR r.reportId < :lastReportId)
            ORDER BY r.reportId DESC
            """)
    List<Report> findByStatusWithCursor(
            @Param("status") String status,
            @Param("lastReportId") Long lastReportId,
            Pageable pageable);

    //TODO: 전체 신고 목록 커서 기반 조회 (관리자용)
    @Query("""
            SELECT r FROM Report r
            WHERE (:lastReportId IS NULL OR r.reportId < :lastReportId)
            ORDER BY r.reportId DESC
            """)
    List<Report> findAllWithCursor(
            @Param("lastReportId") Long lastReportId,
            Pageable pageable);

    //TODO: 유저와 관련된 모든 신고 삭제 (회원 탈퇴 시)
    @Modifying
    @Query("DELETE FROM Report r WHERE r.reporter.userId = :userId OR r.reportedUser.userId = :userId OR r.runningLog.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
