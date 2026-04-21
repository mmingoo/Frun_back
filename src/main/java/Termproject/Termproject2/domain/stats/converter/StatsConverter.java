package Termproject.Termproject2.domain.stats.converter;

import Termproject.Termproject2.domain.stats.dto.StatsSummaryDto;
import Termproject.Termproject2.domain.stats.dto.response.StatsSummaryResponseDto;
import Termproject.Termproject2.domain.stats.entity.RunningStats;
import Termproject.Termproject2.domain.user.entity.User;

public class StatsConverter {


    //TODO: 러닝 통계 객체 컨버터
    public static RunningStats toRunningStats(User user, RunningStats.StatType statType, String statKey) {
        return RunningStats.builder()
                .user(user)
                .statType(statType)
                .statKey(statKey)
                .build();
    }

    //TODO: 러닝 통계 요약 반환 컨버터
    public static StatsSummaryResponseDto toStatsSummaryResponseDto(StatsSummaryDto weekly,
                                                                     StatsSummaryDto monthly) {
        return StatsSummaryResponseDto.builder()
                .weeklyRunDistance(weekly.getTotalDistanceKm())
                .weeklyRunCnt(weekly.getRunCount())
                .weeklyPaceAvg(weekly.getAvgPaceSec())
                .weeklyTotalDurationSec(weekly.getTotalDurationSec())
                .monthlyRunDistance(monthly.getTotalDistanceKm())
                .monthlyRunCnt(monthly.getRunCount())
                .monthlyPaceAvg(monthly.getAvgPaceSec())
                .monthlyTotalDurationSec(monthly.getTotalDurationSec())
                .build();
    }
}
