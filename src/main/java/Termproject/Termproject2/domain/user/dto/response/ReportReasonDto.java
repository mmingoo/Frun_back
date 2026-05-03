package Termproject.Termproject2.domain.user.dto.response;

import lombok.Getter;

@Getter
public class ReportReasonDto {
    private final String reportReason;  // 신고 사유
    private final String actionReason;  // 처리 사유

    public ReportReasonDto(String reportReason, String actionReason) {
        this.reportReason = reportReason;
        this.actionReason = actionReason;
    }
}
