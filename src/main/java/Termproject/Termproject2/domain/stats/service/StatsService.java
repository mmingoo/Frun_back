package Termproject.Termproject2.domain.stats.service;

import Termproject.Termproject2.domain.stats.dto.StatsSummaryDto;
import Termproject.Termproject2.domain.stats.dto.response.MonthlyStatsResponse;
import Termproject.Termproject2.domain.stats.dto.response.PeriodStatsResponse;
import Termproject.Termproject2.domain.stats.dto.response.StatsSummaryResponseDto;
import Termproject.Termproject2.domain.stats.dto.response.WeeklyStatsResponse;

import java.time.LocalDate;

public interface StatsService {
    WeeklyStatsResponse getWeeklyStats(Long userId, LocalDate date);
    MonthlyStatsResponse getMonthlyStats(Long userId, int year, int month);
    PeriodStatsResponse getPeriodStats(Long userId, LocalDate from, LocalDate to);

    StatsSummaryResponseDto getStatSummary(Long userId, int year, int month , LocalDate date);
}
