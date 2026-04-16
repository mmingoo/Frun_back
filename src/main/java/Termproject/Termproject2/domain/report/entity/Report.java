package Termproject.Termproject2.domain.report.entity;

import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.global.common.basedTime.BaseCreatedEntity;
import Termproject.Termproject2.global.common.basedTime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "REPORT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id" ,  nullable = false)
    private User reporter ;

    @Column(name = "report_reason", length = 1000, nullable = false)
    private String reportReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private ReportStatus status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_log_id")
    private RunningLog runningLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_type_id", nullable = false)
    private ReportType reportType;


    @Builder
    public Report(User reporter, User reportedUser, String reportReason,
                  RunningLog runningLog, ReportType reportType , ReportStatus status) {
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reportReason = reportReason;
        this.runningLog = runningLog;
        this.reportType = reportType;
        this.status = status;
    }

}
