package Termproject.Termproject2.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_SANCTION_HISTORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSanctionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sanction_id")
    private Long sanctionId; // 제재 이력 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser; // 제재 대상 유저

    @Enumerated(EnumType.STRING)
    @Column(name = "sanction_type", length = 20, nullable = false)
    private SanctionType sanctionType; // 제재 유형

    @Lob
    @Column(name = "reason", nullable = false)
    private String reason; // 제재 사유

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 제재 일시

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public UserSanctionHistory(User targetUser, SanctionType sanctionType,
                                String reason) {
        this.targetUser = targetUser;
        this.sanctionType = sanctionType;
        this.reason = reason;
    }
}
