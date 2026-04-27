package Termproject.Termproject2.domain.stats.service;

import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.running.service.RunningLogService;
import Termproject.Termproject2.domain.stats.converter.StatsConverter;
import Termproject.Termproject2.domain.stats.dto.*;
import Termproject.Termproject2.domain.stats.dto.response.MonthlyStatsResponse;
import Termproject.Termproject2.domain.stats.dto.response.PeriodStatsResponse;
import Termproject.Termproject2.domain.stats.dto.response.StatsSummaryResponseDto;
import Termproject.Termproject2.domain.stats.dto.response.WeeklyStatsResponse;
import Termproject.Termproject2.domain.stats.entity.RunningStats;
import Termproject.Termproject2.domain.stats.repository.RunningStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final RunningLogService runningLogService;
    private final RunningStatsRepository runningStatsRepository;

    //TODO: 주별 통계 조회
    @Override
    public WeeklyStatsResponse getWeeklyStats(Long userId) {

        LocalDate date = LocalDate.now(); // 현재 날짜
        LocalDate weekStart = date.with(DayOfWeek.MONDAY); // 주 시작 날짜
        LocalDate weekEnd = weekStart.plusDays(6); // 주 종료 날짜

        // RunningStats 테이블에서 weekKey 를 활용해서 요약 stats 조회
        StatsSummaryDto summary = getSummary(userId, RunningStats.StatType.WEEKLY, toWeekKey(date));


        //주에 해당하는 러닝일지(삭제 x , 공개) 조회
        List<RunningLog> logs = runningLogService.findRunningLofBetweenDate(userId, weekStart, weekEnd);

        // 날짜별 거리 합산
        Map<LocalDate, Double> distMap = buildDistanceMap(logs);

        // 차트 생성
        List<DayDistanceDto> chart = buildWeekChart(weekStart, distMap);

        return new WeeklyStatsResponse(summary, chart, Collections.emptyList());
    }

    //TODO: 월별 통계
    @Override
    public MonthlyStatsResponse getMonthlyStats(Long userId, int year, int month) {
        // 2026-03 형태로 반환
        YearMonth ym = YearMonth.of(year, month);

        // 월 시작 날짜 반환
        LocalDate monthStart = ym.atDay(1);

        //월 종료 날짜 반환
        LocalDate monthEnd = ym.atEndOfMonth();

        // RunningStats 테이블에서 monthKey 를 활용해서 요약 stats 조회
        StatsSummaryDto summary = getSummary(userId, RunningStats.StatType.MONTHLY, toMonthKey(year, month));


        // 월에 해당하는 러닝로그 조회(chart용 일별 분해)
        List<RunningLog> logs = runningLogService.findRunningLofBetweenDate(userId, monthStart, monthEnd);

        // 날짜별 거리 합산
        Map<LocalDate, Double> distMap = buildDistanceMap(logs);

        // monthStart가 속한 주의 월요일의 날짜
        LocalDate weekStart = monthStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 월별, 주별 거리 및 각 날짜 거리
        List<MonthlyWeekDto> chart = new ArrayList<>();

        int weekIndex = 1;

        // 월의 마지막 날까지 주 단위로 반복
        while (!weekStart.isAfter(monthEnd)) {

            // 요일 차트 생성
            List<DayDistanceDto> days = buildWeekChart(weekStart, distMap);

            // 주별 총합 거리 합산
            double weekTotal = days.stream().mapToDouble(DayDistanceDto::getDistanceKm).sum();

            // 주차 데이터 (예: "1주", 18.5km, 요일별 거리) 차트에 추가
            chart.add(new MonthlyWeekDto(weekIndex + "주", round1(weekTotal), days));
            weekIndex++;

            // 다음 주 월요일로 이동
            weekStart = weekStart.plusDays(7);
        }

        return new MonthlyStatsResponse(summary, chart, Collections.emptyList());
    }

    //TODO: 기간별 통계
    @Override
    public PeriodStatsResponse getPeriodStats(Long userId, LocalDate from, LocalDate to) {

        List<PeriodDayDto> chart = new ArrayList<>();
        LocalDate cur = from;

        //기간에 해당하는 러닝일지 조회
        List<RunningLog> logs = runningLogService.findRunningLofBetweenDate(userId, from, to);

        // 요약본 생성
        StatsSummaryDto summary = buildSummary(logs);
        Map<LocalDate, Double> distMap = buildDistanceMap(logs);


        // 기간 시작일 부터 기간 종료일까지 진행
        while (!cur.isAfter(to)) {
            // 소수점 첫번째 자리까지 km 계산
            double dist = round1(distMap.getOrDefault(cur, 0.0));

            // 날짜별 거리
            chart.add(new PeriodDayDto(cur.toString(), dayLabel(cur.getDayOfWeek()), dist));
            cur = cur.plusDays(1);
        }

        return new PeriodStatsResponse(summary, chart, Collections.emptyList());
    }

    //TODO: 통계 요약본 조회
    @Override
    public StatsSummaryResponseDto getStatSummary(Long userId, int year, int month, LocalDate date) {

        // 주 통계 요약본
        StatsSummaryDto weeklySummary = getSummary(userId, RunningStats.StatType.WEEKLY, toWeekKey(date));

        // 월 통계 요약본
        StatsSummaryDto monthlySummary = getSummary(userId, RunningStats.StatType.MONTHLY, toMonthKey(year, month));

        return StatsConverter.toStatsSummaryResponseDto(weeklySummary, monthlySummary);
    }


    // ──내부 메서드 ────────────────────────────────────────────────

    //요약본 생성 메서드
    private StatsSummaryDto buildSummary(List<RunningLog> logs) {

        // 로그가 없다면 0으로 값을 채워서 반환
        if (logs.isEmpty()) {
            return new StatsSummaryDto(0.0, 0, 0, 0);
        }

        // 총 거리 합계
        double totalDistanceKm = logs.stream()
                .mapToDouble(l -> l.getDistance().doubleValue())
                .sum();

        // 총 러닝한 초 합계
        int totalDurationSec = logs.stream()
                .mapToInt(l -> l.getDuration().toSecondOfDay())
                .sum();

        // 총 거리 합계가 0 이상이면 평균값 반환
        int avgPaceSec = totalDistanceKm > 0
                ? (int) Math.round(totalDurationSec / totalDistanceKm)
                : 0;

        return new StatsSummaryDto(round1(totalDistanceKm), logs.size(), avgPaceSec, totalDurationSec);
    }

    //날짜별로 거리 합산
    private Map<LocalDate, Double> buildDistanceMap(List<RunningLog> logs) {
        Map<LocalDate, Double> map = new HashMap<>();

        // 반복문 돌면서 거리 합산
        for (RunningLog log : logs) {

            // 같은 날짜에 여러 러닝 로그가 있을 때 거리를 합산
            map.merge(log.getRunDate(), log.getDistance().doubleValue(), Double::sum);
        }

        return map;
    }

    //소수점 첫째자리 반올림
    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    //RunningStats → StatsSummaryDto 변환
    private StatsSummaryDto toSummaryDto(RunningStats stats) {
        double totalDistanceKm = round1(stats.getTotalDistM() / 1000.0); // M -> KM
        int avgPaceSec = (int) Math.round(stats.getAvgPaceSec()); // 평균 페이스 계산
        return new StatsSummaryDto(totalDistanceKm, stats.getRunCount(), avgPaceSec, stats.getTotalDurSec());
    }

    // 값 생성
    private StatsSummaryDto emptySummary() {
        return new StatsSummaryDto(0.0, 0, 0, 0);
    }

    //WeekKey 생성
    private String toWeekKey(LocalDate date) {
        int weekYear = date.get(IsoFields.WEEK_BASED_YEAR);
        int weekNum  = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return weekYear + "-W" + String.format("%02d", weekNum); // 2026-W14 형태로 변환(2026년 14주를 뜻함)
    }

    //MonthKey 생성
    private String toMonthKey(int year, int month) {
        return String.format("%d-%02d", year, month);
    }

    // 주어진 주의 시작일(weekStart)부터 7일간 요일별 거리 데이터 리스트 생성
    // 러닝 기록이 없는 날은 0으로 처리
    private List<DayDistanceDto> buildWeekChart(LocalDate weekStart, Map<LocalDate, Double> distMap) {
        List<DayDistanceDto> days = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            // 월~일 순서로 날짜 계산
            LocalDate day = weekStart.plusDays(i);

            // 해당 날짜 거리 조회, 없으면 0.0
            double dist = round1(distMap.getOrDefault(day, 0.0));

            // day 리스트에 DayDistanceDto(요일, 거리 km ) 추가
            days.add(new DayDistanceDto(dayLabel(day.getDayOfWeek()), dist));
        }
        return days;
    }


    //문자열로 변환
    private String dayLabel(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY    -> "MON";
            case TUESDAY   -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY  -> "THU";
            case FRIDAY    -> "FRI";
            case SATURDAY  -> "SAT";
            case SUNDAY    -> "SUN";
        };
    }


    // 요약 통계 정보 조회
    private StatsSummaryDto getSummary(Long userId, RunningStats.StatType type, String key) {
        return runningStatsRepository
                .findByUserUserIdAndStatTypeAndStatKey(userId, type, key)
                .map(this::toSummaryDto)
                .orElseGet(this::emptySummary);
    }
}
