package Termproject.Termproject2.domain.report.service;

import Termproject.Termproject2.domain.report.converter.ReportConverter;
import Termproject.Termproject2.domain.report.entity.Report;
import Termproject.Termproject2.domain.report.entity.ReportStatus;
import Termproject.Termproject2.domain.report.entity.ReportType;
import Termproject.Termproject2.domain.report.dto.ReportRequestDto;
import Termproject.Termproject2.domain.report.repository.ReportRepository;
import Termproject.Termproject2.domain.report.repository.ReportTypeRepository;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import Termproject.Termproject2.domain.user.service.UserService;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportTypeRepository reportTypeRepository;
    private final UserService userService;
    private final RunningLogRepository runningLogRepository;

    //TODO: 러닝일지 신고 접수
    @Override
    @Transactional
    public void submitRunningLogReport(Long reporterId, Long runningLogId, ReportRequestDto dto) {
        // 신고자 조회
        User reporter = userService.findUserById(reporterId);
        // 러닝 로그 조회
        RunningLog runningLog = runningLogRepository.findById(runningLogId)
                .orElseThrow(()-> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        // 신고 당한 사람 userId
        Long reportedUserId =  runningLog.getUser().getUserId();

        // 신고 대상 사용자 조회
        User reportedUser = userService.findUserById(reporterId);

        // 본인 신고 불가
        if (reporterId.equals(reportedUserId) ){
            throw new BusinessException(ErrorCode.REPORT_SELF_NOT_ALLOWED);
        }

        // 동일 대상에 대한 중복 신고 방지
        if (reportRepository.existsByReporterAndReportedUser(reporter, reportedUser)) {
            throw new BusinessException(ErrorCode.DUPLICATE_REPORT);
        }

        // 신고 유형 조회
        ReportType reportType = reportTypeRepository.findByTypeName("RUNNING_LOG");

        // 신고 저장
        reportRepository.save(ReportConverter.toReport(
                reporter, reportedUser, dto.getReportReason(), runningLog, reportType));
    }
}
