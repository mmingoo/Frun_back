package Termproject.Termproject2.domain.running;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "RUNNING_LOG_IMAGE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningLogImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_image_id")
    private Long logImageId;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_log_id", nullable = false)
    private RunningLog runningLog;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public RunningLogImage(RunningLog runningLog, String imageUrl) {
        this.runningLog = runningLog;
        this.imageUrl = imageUrl;
    }
}
