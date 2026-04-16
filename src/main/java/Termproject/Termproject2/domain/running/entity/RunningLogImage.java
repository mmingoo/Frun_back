package Termproject.Termproject2.domain.running.entity;

import Termproject.Termproject2.global.common.basedTime.BaseCreatedEntity;
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
public class RunningLogImage extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_image_id")
    private Long logImageId;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_log_id", nullable = false)
    private RunningLog runningLog;


    @Builder
    public RunningLogImage(RunningLog runningLog, String imageUrl) {
        this.runningLog = runningLog;
        this.imageUrl = imageUrl;
    }

    public void setRunningLog(RunningLog runningLog) {
        this.runningLog = runningLog;
    }}
