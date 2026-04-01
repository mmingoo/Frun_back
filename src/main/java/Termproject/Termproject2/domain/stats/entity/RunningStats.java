package Termproject.Termproject2.domain.stats.entity;

import Termproject.Termproject2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "stat_type", nullable = false, length = 10)
    private StatType statType;

    @Column(name = "stat_key", nullable = false, length = 20)
    private String statKey;  // ex) "2026-W14", "2026-04"

    @Column(name = "run_count")
    private int runCount = 0;

    @Column(name = "total_dist_m")
    private int totalDistM = 0;

    @Column(name = "total_dur_sec")
    private int totalDurSec = 0;

    // stat_type 열거형
    public enum StatType {
        WEEKLY, MONTHLY
    }

    @Builder
    public RunningStats(User user, StatType statType, String statKey) {
        this.user = user;
        this.statType = statType;
        this.statKey = statKey;
    }

    // 통계 누적 (로그 저장 시)
    public void accumulate(int distM, int durSec) {
        this.runCount    += 1;
        this.totalDistM  += distM;
        this.totalDurSec += durSec;
    }

    // 통계 차감 (로그 삭제 시)
    public void subtract(int distM, int durSec) {
        this.runCount    = Math.max(0, this.runCount - 1);
        this.totalDistM  = Math.max(0, this.totalDistM - distM);
        this.totalDurSec = Math.max(0, this.totalDurSec - durSec);
    }

    // 평균 페이스 계산 (조회 시 사용)
    public double getAvgPaceSec() {
        if (totalDistM == 0) return 0;
        return (double) totalDurSec / (totalDistM / 1000.0);
    }
}