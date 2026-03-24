package Termproject.Termproject2.domain.running.entity;

import Termproject.Termproject2.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RUNNING_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "running_log_id")
    private Long runningLogId;

    @Column(name = "duration", nullable = false)
    private LocalTime duration;

    @Column(name = "run_date", nullable = false)
    private LocalDate runDate;

    @Column(name = "distance", nullable = false, precision = 5, scale = 2)
    private BigDecimal distance;

    @Column(name = "pace", length = 10, nullable = false)
    private String pace;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Lob
    @Column(name = "memo")
    private String memo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private Member user;

    @OneToMany(mappedBy = "runningLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunningLogImage> images = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public RunningLog(Member user, LocalTime duration, LocalDate runDate,
                      BigDecimal distance, String pace, boolean isPublic, String memo) {
        this.user = user;
        this.duration = duration;
        this.runDate = runDate;
        this.distance = distance;
        this.pace = pace;
        this.isPublic = isPublic;
        this.memo = memo;
    }

    public void update(LocalTime duration, LocalDate runDate, BigDecimal distance,
                       String pace, boolean isPublic, String memo) {
        this.duration = duration;
        this.runDate = runDate;
        this.distance = distance;
        this.pace = pace;
        this.isPublic = isPublic;
        this.memo = memo;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
