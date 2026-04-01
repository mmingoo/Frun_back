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


    @GetMapping("/weekly/{userId}")
    @Operation(summary = "주별 통계 조회")
    public ResponseEntity<ApiResponse<WeeklyStatsResponse>> getWeeklyStats(
            @PathVariable Long userId){

        LocalDate date = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(statsService.getWeeklyStats(userId, date)));
    }


    @GetMapping("/monthly/{userId}")
    @Operation(summary = "월별 통계 조회")
    public ResponseEntity<ApiResponse<MonthlyStatsResponse>> getMonthlyStats(
            @PathVariable Long userId){

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        return ResponseEntity.ok(ApiResponse.ok(statsService.getMonthlyStats(userId, year, month)));
    }

    @GetMapping("/period/{userId}")
    @Operation(summary = "기간별 통계 조회")
    public ResponseEntity<ApiResponse<PeriodStatsResponse>> getPeriodStats(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(ApiResponse.ok(statsService.getPeriodStats(userId, from, to)));
    }

     //추후 친구 페이지에서도 친구의 통계 요약본 조회를 위해 userId 를 pathvariable 로 받음
    @GetMapping("/summary/{userId}")
    public ResponseEntity<ApiResponse<StatsSummaryResponseDto>> getStatsSummary(
            @PathVariable Long userId
    ){
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        LocalDate date = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(statsService.getStatSummary(userId,year,month, date)));
    }
}
