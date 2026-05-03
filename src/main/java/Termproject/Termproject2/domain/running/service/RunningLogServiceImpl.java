package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.report.entity.ReportStatus;
import Termproject.Termproject2.domain.report.repository.ReportRepository;
import Termproject.Termproject2.domain.running.converter.RunningLogConverter;
import Termproject.Termproject2.domain.running.dto.request.RunningLogCreateRequest;
import Termproject.Termproject2.domain.running.dto.request.RunningLogUpdateRequest;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.RunningLogCreateResponse;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.entity.RunningLogImage;
import Termproject.Termproject2.domain.comment.repository.CommentRepository;
import Termproject.Termproject2.domain.stats.converter.StatsConverter;
import Termproject.Termproject2.domain.running.repository.LikeRepository;
import Termproject.Termproject2.domain.running.repository.RunningLogImageRepository;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.stats.entity.RunningStats;
import Termproject.Termproject2.domain.stats.repository.RunningStatsRepository;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import Termproject.Termproject2.domain.user.service.UserService;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RunningLogServiceImpl implements RunningLogService {

    private final UserService userService;
    private final RunningLogRepository runningLogRepository;
    private final CommentRepository commentRepository;
    private final RunningLogImageRepository runningLogImageRepository;
    private final ImageService imageService;
    private final LikeRepository likeRepository;
    private final RunningStatsRepository runningStatsRepository;
    private final ReportRepository reportRepository;


    //TODO: 러닝일지 생성
    @Override
    @Transactional
    public RunningLogCreateResponse createRunningLog(Long userId, RunningLogCreateRequest request, List<MultipartFile> images) {
        // 분 초 파싱 , {분, 초}
        int[] duration = parseDuration(request.getDurationMin(), request.getDurationSec());
        User user = userService.findUserById(userId);

        // 날짜 , 시간 검증
        validateRunDateTime(request.getRunDate(), request.getRunTime());

        // 컨버터로 러닝로그 생성
        String pace = calculatePace(duration[0], duration[1], request.getDistance());
        int paceSeconds = calculatePaceSeconds(duration[0], duration[1], request.getDistance());
        RunningLog runningLog = RunningLogConverter.toRunningLog(
                user, request,
                toLocalTime(duration[0], duration[1]),
                pace,
                paceSeconds
        );

        // 러닝 로그 저장
        runningLogRepository.save(runningLog);

        // 공개 일지일 때만 통계 누적
        updateAccumulateStats(request, runningLog, user);

        // 러닝로그 이미지들 저장
        saveRunningLogImages(runningLog, userId, images);

        return new RunningLogCreateResponse(runningLog.getRunningLogId());
    }

    //TODO: 피드 상세 조회
    @Override
    public FriendFeedResponseDto getFeed(Long runningLogId, Long userId) {

        // 러닝 로그 조회
        RunningLog runningLog = findById(runningLogId);

        // 비활성화된 계정의 러닝일지면 400
        isActiveUser(runningLog);

        // 신고 처리 완료된 러닝일지라면 에러
        isReported(runningLog);

        // 삭제된 러닝일지라면 에러
        isDeleted(runningLog);

        //작성자의 userId 추출
        Long authorId = runningLog.getUser().getUserId();

        // 작성자가 본인이 아닐 경우에 러닝일지 공개 여부 검증
        validatePublicAccess(userId, authorId, runningLog);

        // 파일명을 바탕으로 imageUrl 반환
        List<String> imageUrls = resolveImageUrls(runningLog);

        // 좋아요 여부 조회
        boolean liked = likeRepository.existsByUserUserIdAndRunningLogRunningLogId(userId, runningLogId);

        // FriendFeedResponseDto 생성 후 반환
        int commentCtn = (int) commentRepository.countByRunningLogRunningLogId(runningLog.getRunningLogId());

        return RunningLogConverter.toFriendFeedResponseDto(
                runningLog,
                imageService.getProfileImageUrl(runningLog.getUser().getImageUrl()),
                imageUrls, liked, commentCtn
        );
    }

    // 러닝일지가 삭제됐는지 여부
    private void isDeleted(RunningLog runningLog) {
        if (runningLog.isDeleted()) {
            throw new BusinessException(ErrorCode.RUNNING_LOG_IS_DELETED);
        }
    }

    // 신고 처리 완료된 러닝일지인지 여부
    private void isReported(RunningLog runningLog) {
        if (reportRepository.existsByRunningLogRunningLogIdAndStatus(
                runningLog.getRunningLogId(), ReportStatus.COMPLETED)) {
            throw new BusinessException(ErrorCode.RUNNING_LOG_IS_REPORTED);
        }
    }


    //TODO: 러닝일지 수정 (공개 여부 변화에 따라 통계 처리 포함)
    @Override
    @Transactional
    public void updateRunningLog(Long runningLogId, Long userId, RunningLogUpdateRequest request, List<MultipartFile> images) {
        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        validateAuthor(userId, runningLog);
        validateRunDateTime(request.getRunDate(), request.getRunTime());

        boolean wasPublic = runningLog.isPublic();
        boolean willBePublic = request.isPublic();

        // 수정 전 데이터 보존 (변경 전 값으로 통계 차감해야 하므로)
        LocalDate oldDate = runningLog.getRunDate();
        int oldDistM = (int) (runningLog.getDistance().doubleValue() * 1000);
        int oldDurSec = runningLog.getDuration().toSecondOfDay();

        // 러닝로그 업데이트
        setupRunningLog(runningLog, request);

        int newDistM = (int) (runningLog.getDistance().doubleValue() * 1000);
        int newDurSec = runningLog.getDuration().toSecondOfDay();

        // 러닝일지 수정에 따른 통계값 변경
        adjustStatsOnVisibilityChange(
                runningLog.getUser(),
                wasPublic, willBePublic,
                oldDate, oldDistM, oldDurSec,
                runningLog.getRunDate(), newDistM, newDurSec
        );

        //러닝일지 이미지 setup
        setupRunningLogImage(runningLog, request.getKeepImageUrls(), images);
    }


    //TODO: 러닝일지 소프트 삭제 (공개 일지면 통계 차감)
    @Override
    @Transactional
    public void softDeleteRunningLog(Long runningLogId, Long userId) {
        // 러닝일지 조회
        RunningLog runningLog = findById(runningLogId);

        // 러닝일지 삭제 권한 여부 검증
        validateAuthor(userId, runningLog);

        //  공개 일지일 때만 통계 차감
        if (runningLog.isPublic()) {
            int distM = (int) (runningLog.getDistance().doubleValue() * 1000);
            int durSec = runningLog.getDuration().toSecondOfDay();
            subtractStats(runningLog.getUser(), runningLog.getRunDate(), distM, durSec);
        }

        // 러닝로그 삭제 처리
        runningLog.delete();
    }

    //TODO: runningLogId로 러닝일지 조회
    @Override
    public RunningLog findById(Long runningLogId) {
        return runningLogRepository.findById(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));
    }

    //TODO: runningLogId 존재 여부 확인
    @Override
    public boolean existsById(Long runningLogId) {
        return runningLogRepository.existsById(runningLogId);
    }



    // TODO: 삭제되지 않은 공개 러닝일지 찾기
    @Override
    public RunningLog findByNotDeletedAndPublicRunningLog (Long runningLogId){
        return runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));
    }

    // TODO: 특정 범위에 해당하는 삭제되지 않은 공개 러닝일지 찾기
    public List<RunningLog> findRunningLofBetweenDate(Long userId, LocalDate weekStart, LocalDate weekEnd){
        return runningLogRepository
                .findByUserUserIdAndIsDeletedFalseAndIsPublicTrueAndRunDateBetween(userId, weekStart, weekEnd);
    }


    // ===================== 내부 메서드 =====================

    // runDate + runTime 미래 입력 및 최소 날짜(2026-02-01) 이전 입력 검증
    private void validateRunDateTime(LocalDate runDate, LocalTime runTime) {
        LocalDateTime runDateTime = LocalDateTime.of(runDate, runTime);

        // 현재 시간보다 더 먼 미래면
        if (runDateTime.isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.FUTURE_RUN_DATETIME);
        }

        // 2026-02-01 00:00 보다 더 과거면
        if (runDateTime.isBefore(LocalDateTime.of(2026, 2, 1, 0, 0))) {
            throw new BusinessException(ErrorCode.TOO_OLD_RUN_DATETIME);
        }
    }

    // 러닝 로그 설정
    private void setupRunningLog(RunningLog runningLog, RunningLogUpdateRequest request) {
        // 분, 초 파싱 및 유효성 검사
        int[] duration = parseDuration(request.getDurationMin(), request.getDurationSec());
        String pace = calculatePace(duration[0], duration[1], request.getDistance());
        int paceSeconds = calculatePaceSeconds(duration[0], duration[1], request.getDistance());

        runningLog.update(
                toLocalTime(duration[0], duration[1]),
                request.getRunDate(),
                request.getDistance(),
                pace,
                request.isPublic(),
                request.getMemo(),
                request.getRunTime(),
                paceSeconds
        );
    }

    // 러닝 로그 이미지 업데이트
    // - keepImageUrls: 유지할 기존 이미지 파일명 목록 (null이면 기존 이미지 전부 유지)
    // - newImages: 새로 추가할 파일 목록 (null이면 추가 없음)
    private void setupRunningLogImage(RunningLog runningLog, List<String> keepImageUrls, List<MultipartFile> newImages) {
        // URL에서 파일명만 추출
        List<String> keeps = keepImageUrls != null
                ? keepImageUrls.stream()
                .map(this::extractFileName)
                .toList()
                : List.of();
        List<MultipartFile> additions = newImages != null ? newImages : List.of();

        // 이미지 총 개수 제한 체크 (유지 + 새 이미지)
        if (keeps.size() + additions.size() > 5) {
            throw new BusinessException(ErrorCode.TOO_MANY_IMAGES);
        }

        // keepImageUrls에 없는 기존 이미지 삭제
        runningLog.getImages().removeIf(image -> !keeps.contains(image.getImageUrl()));

        // 새 이미지 업로드 후 추가
        saveRunningLogImages(runningLog, runningLog.getUser().getUserId(), additions);
    }

    // 유저가 해당 러닝일지의 작성자인지 검증
    private static void validateAuthor(Long userId, RunningLog runningLog) {
        if (!runningLog.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHORIZATION);
        }
    }

    // 작성자가 본인이 아닐 경우에 러닝일지 공개 여부 검증
    private void validatePublicAccess(Long userId, Long authorId, RunningLog runningLog) {
        if (!userId.equals(authorId) && !runningLog.isPublic()) {
            throw new BusinessException(ErrorCode.PRIVATE_RUNNING_LOG);
        }
    }

    // 분, 초 파싱 및 유효성 검사 (0분 0초 불가)
    private int[] parseDuration(Integer durationMin, Integer durationSec) {
        int min = durationMin != null ? durationMin : 0;
        int sec = durationSec != null ? durationSec : 0;
        if (min == 0 && sec == 0) {
            throw new BusinessException(ErrorCode.INVALID_DURATION);
        }
        return new int[]{min, sec};
    }

    // 이미지 파일 저장 (개수 제한 포함)
    private void saveRunningLogImages(RunningLog runningLog, Long userId, List<MultipartFile> images) {
        // iamges 유효성 검사
        if (images == null || images.isEmpty()) return;

        // image 수가 5개 초과되면 에러 발생
        if (images.size() > 5) {
            throw new BusinessException(ErrorCode.TOO_MANY_IMAGES);
        }

        // 이미지 배치처리
        List<RunningLogImage> imageEntities = images.stream()
                .map(image -> {
                    String fileName = imageService.saveRunningLogImage(userId, image); // 이미지별로 전체 파일명 생성
                    return RunningLogConverter.toRunningLogImage(runningLog, fileName); // runningLogImage 리스트 생성
                })
                .toList();

        //배치 저장
        runningLogImageRepository.saveAll(imageEntities);
    }

    // 파일명을 바탕으로 imageUrl 반환
    private List<String> resolveImageUrls(RunningLog runningLog) {
        return runningLog.getImages().stream()
                .map(image -> imageService.getRunningLogImageUrl(image.getImageUrl()))
                .collect(Collectors.toList());
    }

    // URL에서 파일명만 추출 (슬래시 포함 URL 또는 파일명 그대로 처리)
    private String extractFileName(String url) {
        return url.contains("/") ? url.substring(url.lastIndexOf('/') + 1) : url;
    }

    // 페이스 계산 (총 시간(초) / 거리 → "분'초\"" 형식)
    private String calculatePace(int durationMin, int durationSec, BigDecimal distance) {
        if (distance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DISTANCE);
        }
        int ps = calculatePaceSeconds(durationMin, durationSec, distance);
        return String.format("%d'%02d\"", ps / 60, ps % 60);
    }

    // 페이스 초 단위 계산 (정렬용)
    private int calculatePaceSeconds(int durationMin, int durationSec, BigDecimal distance) {
        long totalSeconds = (long) durationMin * 60 + durationSec;
        return (int) Math.round(totalSeconds / distance.doubleValue());
    }

    // 분, 초를 LocalTime으로 변환
    private LocalTime toLocalTime(int durationMin, int durationSec) {
        int totalSeconds = durationMin * 60 + durationSec;
        return LocalTime.of(totalSeconds / 3600, (totalSeconds % 3600) / 60, totalSeconds % 60);
    }

    //러닝 로그의 작성자인지 검증
    private void isRunningLogAuthor(RunningLog runningLog, Long authorId) {
        // 1. 작성자 일치 여부 확인 (본인 것이 아니면 보안상 NOT_FOUND 처리)
        if (!runningLog.getUser().getUserId().equals(authorId)) {
            throw new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND);
        }
    }

    //활성화 유저의 러닝일지인지 검증
    private void isActiveUser(RunningLog runningLog){
        // 2. 작성자의 계정 상태 확인 (비활성화 계정 여부)
        if (!runningLog.getUser().getUserStatus().equals(UserStatus.ACTIVE)) {
            throw new BusinessException(ErrorCode.USER_INACTIVE_RUNNING_LOG);
        }

    }



    // ===================== RunningStats 유지 메서드 =====================

    // 유효성 검사 후 통계 업데이트(데이터 더하기)
    private void updateAccumulateStats(RunningLogCreateRequest request , RunningLog runningLog, User user){
        if (request.isPublic()) {
            int distM = (int) (request.getDistance().doubleValue() * 1000);
            int durSec = runningLog.getDuration().toSecondOfDay();
            accumulateStats(user, request.getRunDate(), distM, durSec);
        }
    }
    //통계 업데이트(데이터 더하기)
    private void accumulateStats(User user, LocalDate runDate, int distM, int durSec) {
        // 기존 통계값이 있으면 값을 업데이트, 없다면 새로운 통계 데이터 생성
        findOrCreateStats(user, RunningStats.StatType.WEEKLY, toWeekKey(runDate)).accumulate(distM, durSec);
        findOrCreateStats(user, RunningStats.StatType.MONTHLY, toMonthKey(runDate)).accumulate(distM, durSec);
    }


    //통계 데이터 관리(찾기, 생성)
    private RunningStats findOrCreateStats(User user, RunningStats.StatType type, String key) {
        return runningStatsRepository
                .findByUserUserIdAndStatTypeAndStatKey(user.getUserId(), type, key) //StateKey : 26-w13 형태 , Type : WEEKLY, MONTHLY
                .orElseGet(() -> runningStatsRepository.save(  // 데이터가 없다면 생성
                        StatsConverter.toRunningStats(user, type, key)
                ));
    }

    //통계 업데이트(데이터 빼기)
    private void subtractStats(User user, LocalDate runDate, int distM, int durSec) {

        // 주별 통계 데이터 찾기
        // 데이터 가져온 후 통계값 차감
        runningStatsRepository
                .findByUserUserIdAndStatTypeAndStatKey(user.getUserId(), RunningStats.StatType.WEEKLY, toWeekKey(runDate))
                .ifPresent(s -> s.subtract(distM, durSec));
        runningStatsRepository
                .findByUserUserIdAndStatTypeAndStatKey(user.getUserId(), RunningStats.StatType.MONTHLY, toMonthKey(runDate))
                .ifPresent(s -> s.subtract(distM, durSec));
    }
    //stateKey 로 변환
    private String toWeekKey(LocalDate date) {
        int weekYear = date.get(IsoFields.WEEK_BASED_YEAR);
        int weekNum  = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return weekYear + "-W" + String.format("%02d", weekNum);
    }

    //monthKey 로 변환
    private String toMonthKey(LocalDate date) {
        return String.format("%d-%02d", date.getYear(), date.getMonthValue());
    }

    //공개, 비공개 전환 여부에 따른 통계 데이터 수정
    private void adjustStatsOnVisibilityChange(
            User user,
            boolean wasPublic, boolean willBePublic,
            LocalDate oldDate, int oldDistM, int oldDurSec,
            LocalDate newDate, int newDistM, int newDurSec
    ) {
        // 공개 → 공개: 주/월 단위로 변경 여부를 각각 판단 후 통계 조정
        if (wasPublic && willBePublic) {
            adjustWeekStats(user, oldDate, oldDistM, oldDurSec, newDate, newDistM, newDurSec);
            adjustMonthStats(user, oldDate, oldDistM, oldDurSec, newDate, newDistM, newDurSec);

        // 공개 → 비공개: 기존 통계 차감만 수행
        } else if (wasPublic) {
            subtractStats(user, oldDate, oldDistM, oldDurSec);

        // 비공개 → 공개: 새 값 누적만 수행
        } else if (willBePublic) {
            accumulateStats(user, newDate, newDistM, newDurSec);
        }
        // 비공개 → 비공개: 통계 변경 없음
    }

    // 주별 통계 조정: 주가 바뀌었거나 거리/시간이 변경된 경우에만 업데이트
    private void adjustWeekStats(
            User user,
            LocalDate oldDate, int oldDistM, int oldDurSec,
            LocalDate newDate, int newDistM, int newDurSec
    ) {
        String oldKey = toWeekKey(oldDate);
        String newKey = toWeekKey(newDate);
        if (oldKey.equals(newKey) && oldDistM == newDistM && oldDurSec == newDurSec) return;

        runningStatsRepository
                .findByUserUserIdAndStatTypeAndStatKey(user.getUserId(), RunningStats.StatType.WEEKLY, oldKey)
                .ifPresent(s -> s.subtract(oldDistM, oldDurSec));
        findOrCreateStats(user, RunningStats.StatType.WEEKLY, newKey).accumulate(newDistM, newDurSec);
    }

    // 월별 통계 조정: 월이 바뀌었거나 거리/시간이 변경된 경우에만 업데이트
    private void adjustMonthStats(
            User user,
            LocalDate oldDate, int oldDistM, int oldDurSec,
            LocalDate newDate, int newDistM, int newDurSec
    ) {
        String oldKey = toMonthKey(oldDate);
        String newKey = toMonthKey(newDate);
        if (oldKey.equals(newKey) && oldDistM == newDistM && oldDurSec == newDurSec) return;

        runningStatsRepository
                .findByUserUserIdAndStatTypeAndStatKey(user.getUserId(), RunningStats.StatType.MONTHLY, oldKey)
                .ifPresent(s -> s.subtract(oldDistM, oldDurSec));
        findOrCreateStats(user, RunningStats.StatType.MONTHLY, newKey).accumulate(newDistM, newDurSec);
    }


}