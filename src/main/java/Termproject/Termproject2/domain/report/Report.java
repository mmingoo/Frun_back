package Termproject.Termproject2.domain.report;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.member.entity.Member;
import Termproject.Termproject2.domain.running.RunningLog;
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
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private Member user;

    @Lob
    @Column(name = "report_reason", nullable = false)
    private String reportReason;

    @Column(name = "status", length = 30, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

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
    public Report(Member user, String reportReason, Comment comment,
                  RunningLog runningLog, ReportType reportType) {
        this.user = user;
        this.reportReason = reportReason;
        this.comment = comment;
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
