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
    private String imageUrl; // 이미지 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_log_id", nullable = false)
    private RunningLog runningLog; // 연관 러닝일지


    @Builder
    public RunningLogImage(RunningLog runningLog, String imageUrl) {
        this.runningLog = runningLog;
        this.imageUrl = imageUrl;
    }

    public void setRunningLog(RunningLog runningLog) {
        this.runningLog = runningLog;
    }}
