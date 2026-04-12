package Termproject.Termproject2.domain.user.dto.response;

import Termproject.Termproject2.domain.user.entity.UserStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InactiveInfoResponse {
    private final LocalDateTime deactivatedAt;
    private final LocalDateTime deletionScheduledAt;
    private final UserStatus userStatus;

    public InactiveInfoResponse(LocalDateTime deactivatedAt, LocalDateTime deletionScheduledAt, UserStatus userStatus) {
        this.deactivatedAt = deactivatedAt;
        this.deletionScheduledAt = deletionScheduledAt;
        this.userStatus = userStatus;
    }
}
