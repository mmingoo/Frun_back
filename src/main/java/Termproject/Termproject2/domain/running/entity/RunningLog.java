package Termproject.Termproject2.domain.running.entity;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.notification.entity.Notification;
import Termproject.Termproject2.domain.report.entity.Report;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.global.common.basedTime.BaseTimeEntity;
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
public class RunningLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "running_log_id")
    private Long runningLogId;

    @Column(name = "duration", nullable = false)
    private LocalTime duration; // 러닝 총 소요 시간

    @Column(name = "run_date", nullable = false)
    private LocalDate runDate; // 러닝 날짜

    @Column(name = "run_time")
    private LocalTime runTime; // 러닝 시작 시간

    @Column(name = "distance", nullable = false, precision = 5, scale = 2) // 최대 100.00km
    private BigDecimal distance; // 러닝 거리 (km)

    @Column(name = "pace", length = 10, nullable = false)
    private String pace; // 평균 페이스 (분'초" 형식)

    @Column(name = "pace_seconds")
    private Integer paceSeconds; // 정렬용 페이스 초 단위 (pace 저장 시 함께 계산)

    @Column(name = "is_public", nullable = false)
    private boolean isPublic; // 공개 여부

    @Column(name = "like_ctn")
    private int likeCtn; // 좋아요 수

    @Column(name = "memo", length = 500)
    private String memo; // 메모 (최대 500자)

    @Column(name = "delete_reason", length = 1000)
    private String deleteReason; // 삭제 사유

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false; // soft 삭제 여부

    @Column(name = "deletion_scheduled_at")
    private LocalDateTime deletionScheduledAt; // 물리 삭제 예정일 (soft 삭제 후 3개월)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    // Notification은 Comment FK를 가지므로 comments cascade 전에 선언
    @OneToMany(mappedBy = "runningLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "runningLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "runningLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "runningLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "runningLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunningLogImage> images = new ArrayList<>();

    @Builder
    public RunningLog(User user, LocalTime duration, LocalDate runDate,
                      BigDecimal distance, String pace, boolean isPublic, String memo, LocalTime runTime, Integer paceSeconds) {
        this.user = user;
        this.duration = duration;
        this.runDate = runDate;
        this.distance = distance;
        this.pace = pace;
        this.isPublic = isPublic;
        this.memo = memo;
        this.runTime = runTime;
        this.paceSeconds = paceSeconds;
    }

    public void update(LocalTime duration, LocalDate runDate, BigDecimal distance,
                       String pace, boolean isPublic, String memo, LocalTime runTime, Integer paceSeconds) {
        this.duration = duration;
        this.runDate = runDate;
        this.distance = distance;
        this.memo = memo;
        this.isPublic = isPublic;
        this.pace = pace;
        this.runTime = runTime;
        this.paceSeconds = paceSeconds;
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


    public void minusLikeCnt(){
        this.likeCtn -= 1;
    }

}
