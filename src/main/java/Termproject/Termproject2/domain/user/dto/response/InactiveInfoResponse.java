package Termproject.Termproject2.domain.user.dto.response;

import Termproject.Termproject2.domain.user.entity.UserStatus;
import lombok.Getter;

import java.time.LocalDateTime;

// 비활성화 계정 정보 응답
@Getter
public class InactiveInfoResponse {
    private final LocalDateTime deactivatedAt; // 비활성화 일시
    private final LocalDateTime deletionScheduledAt; // 삭제 예정 일시
    private final UserStatus userStatus; // 비활성화 상태 유형

    public InactiveInfoResponse(LocalDateTime deactivatedAt, LocalDateTime deletionScheduledAt, UserStatus userStatus) {
        this.deactivatedAt = deactivatedAt;
        this.deletionScheduledAt = deletionScheduledAt;
        this.userStatus = userStatus;
    }
}
