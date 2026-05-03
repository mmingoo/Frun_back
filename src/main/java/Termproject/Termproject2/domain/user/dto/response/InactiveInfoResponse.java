package Termproject.Termproject2.domain.user.dto.response;

import Termproject.Termproject2.domain.user.entity.UserStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// 비활성화 계정 정보 응답
@Getter
public class InactiveInfoResponse {
    private final LocalDateTime deactivatedAt;
    private final LocalDateTime deletionScheduledAt;
    private final UserStatus userStatus;
    private final List<ReportReasonDto> reportReasons; // REPORT_INACTIVE 일 때만 값 있음, 그 외 null
    private final String adminReason;                  // DIRECT_INACTIVE 일 때만 값 있음, 그 외 null

    public InactiveInfoResponse(LocalDateTime deactivatedAt, LocalDateTime deletionScheduledAt,
                                UserStatus userStatus, List<ReportReasonDto> reportReasons, String adminReason) {
        this.deactivatedAt = deactivatedAt;
        this.deletionScheduledAt = deletionScheduledAt;
        this.userStatus = userStatus;
        this.reportReasons = reportReasons;
        this.adminReason = adminReason;
    }
}
