package Termproject.Termproject2.domain.report;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "REPORT_ACTION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportAction {

    @Id
    @Column(name = "report_id")
    private Long reportId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @Column(name = "action_type", length = 30, nullable = false)
    private String actionType;

    @Lob
    @Column(name = "action_reason", nullable = false)
    private String actionReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public ReportAction(Report report, String actionType, String actionReason) {
        this.report = report;
        this.actionType = actionType;
        this.actionReason = actionReason;
    }
}
