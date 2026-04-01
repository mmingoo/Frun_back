package Termproject.Termproject2.domain.stats.service;

import Termproject.Termproject2.domain.stats.dto.MonthlyStatsResponse;
import Termproject.Termproject2.domain.stats.dto.MySummaryResponse;
import Termproject.Termproject2.domain.stats.dto.PeriodStatsResponse;
import Termproject.Termproject2.domain.stats.dto.WeeklyStatsResponse;

import java.time.LocalDate;

public interface StatsService {
    WeeklyStatsResponse getWeeklyStats(Long userId, LocalDate date);
    MonthlyStatsResponse getMonthlyStats(Long userId, int year, int month);
    PeriodStatsResponse getPeriodStats(Long userId, LocalDate from, LocalDate to);
}
