package Termproject.Termproject2.domain.stats.service;

import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.stats.dto.MySummaryResponse;
import Termproject.Termproject2.domain.stats.dto.RunSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final RunningLogRepository runningLogRepository;

    @Override
    public MySummaryResponse getMySummary(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate monthStart = today.withDayOfMonth(1);

        List<RunningLog> weeklyLogs = runningLogRepository
                .findByUserUserIdAndIsDeletedFalseAndRunDateBetween(userId, weekStart, today);
        List<RunningLog> monthlyLogs = runningLogRepository
                .findByUserUserIdAndIsDeletedFalseAndRunDateBetween(userId, monthStart, today);

        return new MySummaryResponse(aggregate(weeklyLogs), aggregate(monthlyLogs));
    }

    private RunSummaryDto aggregate(List<RunningLog> logs) {
        if (logs.isEmpty()) {
            return new RunSummaryDto(0.0, 0, null);
        }

        double totalDistanceKm = logs.stream()
                .mapToDouble(l -> l.getDistance().doubleValue())
                .sum();
        long totalDurationSeconds = logs.stream()
                .mapToLong(l -> l.getDuration().toSecondOfDay())
                .sum();

        String avgPace = null;
        if (totalDistanceKm > 0) {
            long avgPaceSeconds = Math.round(totalDurationSeconds / totalDistanceKm);
            long minutes = avgPaceSeconds / 60;
            long seconds = avgPaceSeconds % 60;
            avgPace = String.format("%d'%02d\"", minutes, seconds);
        }

        double roundedDistance = Math.round(totalDistanceKm * 10.0) / 10.0;
        return new RunSummaryDto(roundedDistance, logs.size(), avgPace);
    }
}
