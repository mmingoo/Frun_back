package Termproject.Termproject2.domain.report.repository;

import Termproject.Termproject2.domain.report.Report;
import Termproject.Termproject2.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * 동일한 신고자·대상 조합의 중복 신고 여부 확인
     * (같은 사람이 같은 대상을 같은 이유로 여러 번 신고하는 것을 방지)
     */
    boolean existsByReporterAndReportedUser(User reporter, User reportedUser);

    /**
     * 상태별 신고 목록 조회 (관리자용, 커서 기반 페이징)
     * lastReportId가 null이면 처음부터, 아니면 해당 id 이후부터 조회
     */
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

    /**
     * 전체 신고 목록 조회 (관리자용, 커서 기반 페이징)
     */
    @Query("""
            SELECT r FROM Report r
            WHERE (:lastReportId IS NULL OR r.reportId < :lastReportId)
            ORDER BY r.reportId DESC
            """)
    List<Report> findAllWithCursor(
            @Param("lastReportId") Long lastReportId,
            Pageable pageable);
}
