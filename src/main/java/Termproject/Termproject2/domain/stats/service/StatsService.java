package Termproject.Termproject2.domain.stats.service;

import Termproject.Termproject2.domain.stats.dto.StatsSummaryDto;
import Termproject.Termproject2.domain.stats.dto.response.MonthlyStatsResponse;
import Termproject.Termproject2.domain.stats.dto.response.PeriodStatsResponse;
import Termproject.Termproject2.domain.stats.dto.response.StatsSummaryResponseDto;
import Termproject.Termproject2.domain.stats.dto.response.WeeklyStatsResponse;

import java.time.LocalDate;

public interface StatsService {
    //TODO: 주별 통계 조회
    WeeklyStatsResponse getWeeklyStats(Long userId);

    //TODO: 월별 통계 조회
    MonthlyStatsResponse getMonthlyStats(Long userId, int year, int month);

    //TODO: 기간별 통계 조회
    PeriodStatsResponse getPeriodStats(Long userId, LocalDate from, LocalDate to);

    //TODO: 통계 요약 조회 (주/월 기준)
    StatsSummaryResponseDto getStatSummary(Long userId, int year, int month , LocalDate date);
}
