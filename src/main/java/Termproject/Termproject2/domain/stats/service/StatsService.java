package Termproject.Termproject2.domain.stats.service;

import Termproject.Termproject2.domain.stats.dto.MySummaryResponse;

public interface StatsService {
    MySummaryResponse getMySummary(Long userId);
}
