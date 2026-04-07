package Termproject.Termproject2.domain.report.service;

import Termproject.Termproject2.domain.report.dto.ReportRequestDto;

public interface ReportService {

    /**
     * 신고 접수
     * @param reporterId 신고자 userId
     * @param dto        신고 요청 데이터
     */
    void submitRunningLogReport(Long reporterId, Long runningLogId, ReportRequestDto dto);
}
