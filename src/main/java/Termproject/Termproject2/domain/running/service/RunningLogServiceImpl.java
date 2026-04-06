package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.request.RunningLogCreateRequest;
import Termproject.Termproject2.domain.running.dto.request.RunningLogUpdateRequest;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.RunningLogCreateResponse;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.entity.RunningLogImage;
import Termproject.Termproject2.domain.running.repository.LikeRepository;
import Termproject.Termproject2.domain.running.repository.RunningLogImageRepository;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.stats.entity.RunningStats;
import Termproject.Termproject2.domain.stats.repository.RunningStatsRepository;
import Termproject.Termproject2.domain.user.entity.User;
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
    private final RunningLogImageRepository runningLogImageRepository;
    private final ImageService imageService;
    private final LikeRepository likeRepository;
    private final RunningStatsRepository runningStatsRepository;

@Override
@Transactional
public RunningLogCreateResponse createRunningLog(Long userId, RunningLogCreateRequest request, List<MultipartFile> images) {
    int[] duration = parseDuration(request.getDurationMin(), request.getDurationSec());
    User user = userService.findById(userId);

    RunningLog runningLog = RunningLog.builder()
            .user(user)
            .runDate(request.getRunDate())
            .duration(toLocalTime(duration[0], duration[1]))
            .distance(request.getDistance())
            .memo(request.getMemo())
            .isPublic(request.isPublic())
            .pace(calculatePace(duration[0], duration[1], request.getDistance()))
            .build();

    runningLogRepository.save(runningLog);

    // 공개 일지일 때만 통계 누적
    if (request.isPublic()) {
        System.out.println("공개 여부 : " + request.isPublic());
        int distM = (int) (request.getDistance().doubleValue() * 1000);
        int durSec = runningLog.getDuration().toSecondOfDay();
        accumulateStats(user, request.getRunDate(), distM, durSec);
    }

    saveRunningLogImages(runningLog, userId, images);
    return new RunningLogCreateResponse(runningLog.getRunningLogId());
}

    // 피드 상세 조회
    @Override
    public FriendFeedResponseDto getFeed(Long runningLogId, Long authorId, Long userId) {
        // 러닝 로그 조회(삭제되지 않은 러닝일지에 한해서)
        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        // 작성자가 본인이 아닐 경우에 러닝일지 공개 여부 검증
        validatePublicAccess(userId, authorId, runningLog);

        // 파일명을 바탕으로 imageUrl 반환
        List<String> imageUrls = resolveImageUrls(runningLog);

        // 좋아요 여부 조회
        boolean liked = likeRepository.existsByUserUserIdAndRunningLogRunningLogId(userId, runningLogId);

        // FriendFeedResponseDto 생성 후 반환
        return toFriendFeedResponseDto(runningLog, imageUrls, liked);
    }

    // 공개 여부 변화에 따라 통계 처리
    @Override
    @Transactional
    public void updateRunningLog(Long runningLogId, Long userId, RunningLogUpdateRequest request, List<MultipartFile> images) {
        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        validateAuthor(userId, runningLog);

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

        //  공개 여부 변화에 따른 통계 처리
        if (wasPublic && willBePublic) {
            // 공개 → 공개: 기존 값 차감 후 새 값 누적
            System.out.println("공개 → 공개");
            subtractStats(runningLog.getUser(), oldDate, oldDistM, oldDurSec);
            accumulateStats(runningLog.getUser(), runningLog.getRunDate(), newDistM, newDurSec);
        } else if (wasPublic && !willBePublic) {
            System.out.println("공개 → 비공개");
            // 공개 → 비공개: 기존 값 차감만
            subtractStats(runningLog.getUser(), oldDate, oldDistM, oldDurSec);
        } else if (!wasPublic && willBePublic) {
            System.out.println(" 비공개 → 공개");
            // 비공개 → 공개: 새 값 누적만
            accumulateStats(runningLog.getUser(), runningLog.getRunDate(), newDistM, newDurSec);
        }
        // 비공개 → 비공개: 아무것도 하지 않음

        setupRunningLogImage(runningLog, request.getKeepImageUrls(), images);
    }

//    @Override
//    @Transactional
//    public void softDeleteRunningLog(Long runningLogId, Long userId) {
//        // RunningLog 조회
//        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));
//
//        // 유저가 작성자인지 검증
//        validateAuthor(userId, runningLog);
//
//        // RunningStats 차감
//        int distM = (int) (runningLog.getDistance().doubleValue() * 1000);
//        int durSec = runningLog.getDuration().toSecondOfDay();
//        subtractStats(runningLog.getUser(), runningLog.getRunDate(), distM, durSec);
//
//        // soft 삭제
//        runningLog.delete();
//    }

    //공개 일지일 때만 통계 차감
    @Override
    @Transactional
    public void softDeleteRunningLog(Long runningLogId, Long userId) {
        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        validateAuthor(userId, runningLog);

        //  공개 일지일 때만 통계 차감
        if (runningLog.isPublic()) {
            int distM = (int) (runningLog.getDistance().doubleValue() * 1000);
            int durSec = runningLog.getDuration().toSecondOfDay();
            subtractStats(runningLog.getUser(), runningLog.getRunDate(), distM, durSec);
        }

        runningLog.delete();
    }

    @Override
    public RunningLog findById(Long runningLogId) {
        return runningLogRepository.findById(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));
    }

    @Override
    public boolean existsById(Long runningLogId) {
        return runningLogRepository.existsById(runningLogId);
    }

    // 러닝 로그 설정
    public void setupRunningLog(RunningLog runningLog, RunningLogUpdateRequest request) {
        // 분, 초 파싱 및 유효성 검사
        int[] duration = parseDuration(request.getDurationMin(), request.getDurationSec());

        runningLog.update(
                toLocalTime(duration[0], duration[1]),
                request.getRunDate(),
                request.getDistance(),
                calculatePace(duration[0], duration[1], request.getDistance()),
                request.isPublic(),
                request.getMemo(),
                request.getRunTime()
        );
    }

    // 러닝 로그 이미지 업데이트
    // - keepImageUrls: 유지할 기존 이미지 파일명 목록 (null이면 기존 이미지 전부 유지)
    // - newImages: 새로 추가할 파일 목록 (null이면 추가 없음)
    public void setupRunningLogImage(RunningLog runningLog, List<String> keepImageUrls, List<MultipartFile> newImages) {
        // URL에서 파일명만 추출
        List<String> keeps = keepImageUrls != null
                ? keepImageUrls.stream()
                .map(this::extractFileName)
                .collect(Collectors.toList())
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

    // ===================== private 메서드 =====================

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
        if (images == null || images.isEmpty()) return;

        if (images.size() > 5) {
            throw new BusinessException(ErrorCode.TOO_MANY_IMAGES);
        }

        // 각 이미지 저장
        for (MultipartFile image : images) {
            String fileName = imageService.saveRunningLogImage(userId, image);
            runningLogImageRepository.save(
                    RunningLogImage.builder()
                            .runningLog(runningLog)
                            .imageUrl(fileName)
                            .build()
            );
        }
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

    // RunningLog -> FriendFeedResponseDto 변환
    private FriendFeedResponseDto toFriendFeedResponseDto(RunningLog runningLog, List<String> imageUrls, boolean liked) {
        User author = runningLog.getUser();
        FriendFeedResponseDto dto = new FriendFeedResponseDto(
                runningLog.getRunningLogId(), author.getUserId(), author.getNickName(),
                imageService.getProfileImageUrl(author.getImageUrl()),
                runningLog.getRunDate(), runningLog.getRunTime(), runningLog.getDistance(), runningLog.getPace(),
                runningLog.getDuration(), runningLog.getMemo(), runningLog.getCreatedAt(),
                runningLog.getCommentCtn(), runningLog.getLikeCtn(),
                liked, imageUrls , runningLog.isPublic()
        );
        dto.setLiked(liked);
        return dto;
    }

    // 페이스 계산 (총 시간(초) / 거리 → "분'초\"" 형식)
    private String calculatePace(int durationMin, int durationSec, BigDecimal distance) {
        if (distance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DISTANCE);
        }
        long totalSeconds = (long) durationMin * 60 + durationSec;
        long paceSeconds = Math.round(totalSeconds / distance.doubleValue());
        return String.format("%d'%02d\"", paceSeconds / 60, paceSeconds % 60);
    }

    // 분, 초를 LocalTime으로 변환
    private LocalTime toLocalTime(int durationMin, int durationSec) {
        int totalSeconds = durationMin * 60 + durationSec;
        return LocalTime.of(totalSeconds / 3600, (totalSeconds % 3600) / 60, totalSeconds % 60);
    }

    // ===================== RunningStats 유지 메서드 =====================

    //TODO : 통계 업데이트(데이터 더하기)
    private void accumulateStats(User user, LocalDate runDate, int distM, int durSec) {
        // 기존 통계값이 있으면 값을 업데이트, 없다면 새로운 통계 데이터 생성
        findOrCreateStats(user, RunningStats.StatType.WEEKLY, toWeekKey(runDate)).accumulate(distM, durSec);
        findOrCreateStats(user, RunningStats.StatType.MONTHLY, toMonthKey(runDate)).accumulate(distM, durSec);
    }

    //TODO : 통계 업데이트(데이터 빼기)
    private void subtractStats(User user, LocalDate runDate, int distM, int durSec) {
        System.out.println("데이터 빼기 실행");
        System.out.println("distM : " + distM + "durSec : " + durSec);
        // 주별 통계 데이터 찾기
        // 데이터 가져온 후 통계값 차감
        runningStatsRepository
                .findByUserUserIdAndStatTypeAndStatKey(user.getUserId(), RunningStats.StatType.WEEKLY, toWeekKey(runDate))
                .ifPresent(s -> s.subtract(distM, durSec));
        runningStatsRepository
                .findByUserUserIdAndStatTypeAndStatKey(user.getUserId(), RunningStats.StatType.MONTHLY, toMonthKey(runDate))
                .ifPresent(s -> s.subtract(distM, durSec));
    }

    //TODO : 통계 데이터 관리(찾기, 생성)
    private RunningStats findOrCreateStats(User user, RunningStats.StatType type, String key) {
        return runningStatsRepository
                .findByUserUserIdAndStatTypeAndStatKey(user.getUserId(), type, key) //StateKey : 26-w13 형태 , Type : WEEKLY, MONTHLY
                .orElseGet(() -> runningStatsRepository.save(  // 데이터가 없다면 생성
                        RunningStats.builder().user(user).statType(type).statKey(key).build()
                ));
    }

    //TODO: stateKey 로 변환
    private String toWeekKey(LocalDate date) {
        int weekYear = date.get(IsoFields.WEEK_BASED_YEAR);
        int weekNum  = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return weekYear + "-W" + String.format("%02d", weekNum);
    }

    //TODO: monthKey 로 변환
    private String toMonthKey(LocalDate date) {
        return String.format("%d-%02d", date.getYear(), date.getMonthValue());
    }
}