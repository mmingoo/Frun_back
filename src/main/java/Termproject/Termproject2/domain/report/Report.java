package Termproject.Termproject2.domain.report;

import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.running.entity.RunningLog;
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
public class Report {

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

    @Lob
    @Column(name = "report_reason", nullable = false)
    private String reportReason;

    @Column(name = "status", length = 30, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_log_id")
    private RunningLog runningLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_type_id", nullable = false)
    private ReportType reportType;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = "PENDING";
    }

    @Builder
    public Report(User reporter, User reportedUser, String reportReason,
                  RunningLog runningLog, ReportType reportType) {
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reportReason = reportReason;
        this.runningLog = runningLog;
        this.reportType = reportType;
    }

    public void complete() {
        this.status = "COMPLETED";
    }

    public void reject() {
        this.status = "REJECTED";
    }
}
