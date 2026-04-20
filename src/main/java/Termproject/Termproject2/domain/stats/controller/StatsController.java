package Termproject.Termproject2.domain.stats.controller;

import Termproject.Termproject2.domain.stats.dto.response.MonthlyStatsResponse;
import Termproject.Termproject2.domain.stats.dto.response.PeriodStatsResponse;
import Termproject.Termproject2.domain.stats.dto.response.StatsSummaryResponseDto;
import Termproject.Termproject2.domain.stats.dto.response.WeeklyStatsResponse;
import Termproject.Termproject2.domain.stats.service.StatsService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stats")
public class StatsController {

    private final JwtTokenExtractor jwtTokenExtractor;
    private final StatsService statsService;


    /**
     * [GET] /api/v1/stats/weekly/{userId}
     * 주별 통계 조회 - 현재 주 기준 요일별 거리 및 요약 반환
     */
    @GetMapping("/weekly/{userId}")
    @Operation(summary = "주별 통계 조회")
    public ResponseEntity<ApiResponse<WeeklyStatsResponse>> getWeeklyStats(
            @PathVariable Long userId){

        LocalDate date = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(statsService.getWeeklyStats(userId, date)));
    }


    /**
     * [GET] /api/v1/stats/monthly/{userId}
     * 월별 통계 조회 - 현재 월 기준 주차별 거리 및 요약 반환
     */
    @GetMapping("/monthly/{userId}")
    @Operation(summary = "월별 통계 조회")
    public ResponseEntity<ApiResponse<MonthlyStatsResponse>> getMonthlyStats(
            @PathVariable Long userId){

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        return ResponseEntity.ok(ApiResponse.ok(statsService.getMonthlyStats(userId, year, month)));
    }

    /**
     * [GET] /api/v1/stats/period/{userId}
     * 기간별 통계 조회 - from~to 범위의 일별 거리 및 요약 반환
     */
    @GetMapping("/period/{userId}")
    @Operation(summary = "기간별 통계 조회")
    public ResponseEntity<ApiResponse<PeriodStatsResponse>> getPeriodStats(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(ApiResponse.ok(statsService.getPeriodStats(userId, from, to)));
    }

    /**
     * [GET] /api/v1/stats/summary/{userId}
     * 통계 요약 조회 - 주/월 기준 거리·횟수·페이스·시간 반환 (사이드바용)
     */
    @GetMapping("/summary/{userId}")
    @Operation(summary = "통계 요약 조회" , description = "메인 화면 사이드바에 있는 통계 요약 조회 api 입니다")

    public ResponseEntity<ApiResponse<StatsSummaryResponseDto>> getStatsSummary(
            @PathVariable Long userId
    ){
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        LocalDate date = LocalDate.now();

        return ResponseEntity.ok(ApiResponse.ok(statsService.getStatSummary(userId,year,month, date)));
    }
}
