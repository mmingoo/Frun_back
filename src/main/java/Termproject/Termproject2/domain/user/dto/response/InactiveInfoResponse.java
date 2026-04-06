package Termproject.Termproject2.domain.user.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InactiveInfoResponse {
    private final LocalDateTime deactivatedAt;
    private final LocalDateTime deletionScheduledAt;

    public InactiveInfoResponse(LocalDateTime deactivatedAt, LocalDateTime deletionScheduledAt) {
        this.deactivatedAt = deactivatedAt;
        this.deletionScheduledAt = deletionScheduledAt;
    }
}
