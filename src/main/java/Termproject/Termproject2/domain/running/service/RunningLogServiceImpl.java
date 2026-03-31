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
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.service.UserService;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalTime;
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

    @Override
    @Transactional
    public RunningLogCreateResponse createRunningLog(Long userId, RunningLogCreateRequest request, List<MultipartFile> images) {

        // 분, 초 파싱 및 유효성 검사
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

        // 이미지가 있으면 각각 저장
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

    @Override
    @Transactional
    public void updateRunningLog(Long runningLogId, Long userId, RunningLogUpdateRequest request, List<MultipartFile> images) {

        // 러닝일지 조회 (삭제된 로그 제외)
        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        // 유저가 해당 러닝일지의 작성자인지 검증
        validateAuthor(userId, runningLog);

        // 러닝로그 업데이트
        setupRunningLog(runningLog, request);

        // 러닝로그 이미지 업데이트
        setupRunningLogImage(runningLog, request.getKeepImageUrls(), images);
    }

    @Override
    @Transactional
    public void softDeleteRunningLog(Long runningLogId, Long userId) {
        // RunningLog 조회
        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        // 유저가 작성자인지 검증
        validateAuthor(userId, runningLog);

        // soft 삭제
        runningLog.delete();
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
                liked, imageUrls
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
}