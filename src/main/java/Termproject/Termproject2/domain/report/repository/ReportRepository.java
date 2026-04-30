package Termproject.Termproject2.domain.report.repository;

import Termproject.Termproject2.domain.report.entity.Report;
import Termproject.Termproject2.domain.report.entity.ReportStatus;
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

    // 특정 러닝일지에 대해 처리 완료된 신고 존재 여부 확인
    boolean existsByRunningLogRunningLogIdAndStatus(Long runningLogId, ReportStatus status);

}
