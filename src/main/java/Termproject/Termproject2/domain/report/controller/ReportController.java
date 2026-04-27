package Termproject.Termproject2.domain.report.controller;

import Termproject.Termproject2.domain.report.dto.ReportRequestDto;
import Termproject.Termproject2.domain.report.service.ReportService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    /**
     * [POST] /api/v1/reports
     * 신고 접수 - 로그인한 사용자가 특정 사용자(또는 러닝일지)를 신고
     * 본인 신고 및 중복 신고는 불가
     */
    @PostMapping("/{runningLogId}")
    @Operation(summary = "신고 접수", description = "러닝일지 신고 사항을 접수합니다. 본인 신고 및 중복 신고는 불가합니다.")
    public ResponseEntity<ApiResponse<?>> submitReport(
            @Valid @RequestBody ReportRequestDto dto,
            @PathVariable Long runningLogId
    ) {

        Long reporterId = JwtTokenExtractor.getUserId();
        reportService.submitRunningLogReport(reporterId,runningLogId, dto);

        return ResponseEntity.ok(ApiResponse.ok("신고가 접수되었습니다."));
    }
}
