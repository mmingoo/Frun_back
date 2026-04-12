package Termproject.Termproject2.domain.running.entity;

import Termproject.Termproject2.domain.user.entity.User;
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

    @Column(name = "run_time", nullable = false)
    private LocalTime runTime;


    @Column(name = "distance", nullable = false, precision = 5, scale = 2) // 최대 100.00km
    private BigDecimal distance;

    @Column(name = "pace", length = 10, nullable = false)
    private String pace;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "comment_ctn")
    private int commentCtn;

    @Column(name = "like_ctn")
    private int likeCtn;

    @Column(name = "memo", length = 500)
    private String memo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deletion_scheduled_at")
    private LocalDateTime deletionScheduledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

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
    public RunningLog(User user, LocalTime duration, LocalDate runDate,
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
                       String pace, boolean isPublic, String memo , LocalTime runTime) {
        this.duration = duration;
        this.runDate = runDate;
        this.distance = distance;
        this.memo = memo;
        this.isPublic = isPublic;
        this.pace = pace;
        this.runTime = runTime;

    }

    public void delete() {
        this.isDeleted = true;
        this.deletionScheduledAt = LocalDateTime.now().plusMonths(3);
    }

    public void addImage(RunningLogImage image){
        this.images.add(image);

        if(image.getRunningLog() != this){
            image.setRunningLog(this);
        }
    }

    public void addLikeCnt(){
        this.likeCtn += 1;
    }

    public void minusLikeCnt(){
        this.likeCtn -= 1;
    }

}
