package Termproject.Termproject2.domain.stats.controller;

import Termproject.Termproject2.domain.stats.dto.MonthlyStatsResponse;
import Termproject.Termproject2.domain.stats.dto.PeriodStatsResponse;
import Termproject.Termproject2.domain.stats.dto.WeeklyStatsResponse;
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
     * GET /api/v1/stats/{userId}/weekly?date=2026-04-01
     *
     * 응답:
     * {
     *   "summary": { "totalDistanceKm": 23.5, "runCount": 4, "avgPaceSec": 362, "totalDurationSec": 13920 },
     *   "chart": [
     *     { "dayOfWeek": "MON", "distanceKm": 0 },
     *     { "dayOfWeek": "TUE", "distanceKm": 5.2 },
     *     ...
     *   ],
     *   "friendStats": [...]
     * }
     */
    @GetMapping("/{userId}/weekly")
    @Operation(summary = "주별 통계 조회")
    public ResponseEntity<ApiResponse<WeeklyStatsResponse>> getWeeklyStats(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(ApiResponse.ok(statsService.getWeeklyStats(userId, date)));
    }

    /**
     * GET /api/v1/stats/{userId}/monthly?year=2026&month=4
     *
     * 응답:
     * {
     *   "summary": { ... },
     *   "chart": [
     *     { "weekLabel": "1주", "totalDistanceKm": 18.5, "days": [{ "dayOfWeek": "MON", "distanceKm": 0 }, ...] },
     *     { "weekLabel": "2주", ... },
     *     ...
     *   ],
     *   "friendStats": [...]
     * }
     */
    @GetMapping("/{userId}/monthly")
    @Operation(summary = "월별 통계 조회")
    public ResponseEntity<ApiResponse<MonthlyStatsResponse>> getMonthlyStats(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {

        return ResponseEntity.ok(ApiResponse.ok(statsService.getMonthlyStats(userId, year, month)));
    }

    /**
     * GET /api/v1/stats/{userId}/period?from=2026-03-01&to=2026-03-17
     *
     * 응답:
     * {
     *   "summary": { ... },
     *   "chart": [
     *     { "date": "2026-03-01", "dayOfWeek": "SAT", "distanceKm": 5.2 },
     *     { "date": "2026-03-02", "dayOfWeek": "SUN", "distanceKm": 0 },
     *     ...
     *   ],
     *   "friendStats": [...]
     * }
     */
    @GetMapping("/{userId}/period")
    @Operation(summary = "기간별 통계 조회")
    public ResponseEntity<ApiResponse<PeriodStatsResponse>> getPeriodStats(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(ApiResponse.ok(statsService.getPeriodStats(userId, from, to)));
    }
}
