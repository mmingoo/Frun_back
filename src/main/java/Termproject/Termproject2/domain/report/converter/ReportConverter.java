package Termproject.Termproject2.domain.report.converter;

import Termproject.Termproject2.domain.report.entity.Report;
import Termproject.Termproject2.domain.report.entity.ReportStatus;
import Termproject.Termproject2.domain.report.entity.ReportType;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.user.entity.User;

public class ReportConverter {

    //TODO: 신고 객체로 변환하는 dto
    public static Report toReport(User reporter, User reportedUser, String reportReason,
                                   RunningLog runningLog, ReportType reportType) {
        return Report.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reportReason(reportReason)
                .runningLog(runningLog)
                .reportType(reportType)
                .status(ReportStatus.PENDING)
                .build();
    }
}
