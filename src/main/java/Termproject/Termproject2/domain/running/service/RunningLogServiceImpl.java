package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.request.RunningLogCreateRequest;
import Termproject.Termproject2.domain.running.dto.request.RunningLogUpdateRequest;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.RunningLogCreateResponse;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.entity.RunningLogImage;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RunningLogServiceImpl implements RunningLogService {

    private final UserService userService;
    private final RunningLogRepository runningLogRepository;
    private final RunningLogImageRepository runningLogImageRepository;
    private final ImageService imageService;

    @Override
    @Transactional
    public RunningLogCreateResponse createRunningLog(Long userId, RunningLogCreateRequest request, List<MultipartFile> images) {

        // 분, 초 구하기
        int durationMin = request.getDurationMin() != null ? request.getDurationMin() : 0;
        int durationSec = request.getDurationSec() != null ? request.getDurationSec() : 0;

        // 분, 초 유효성 검사
        if (durationMin == 0 && durationSec == 0) {
            throw new BusinessException(ErrorCode.INVALID_DURATION);
        }

        User user = userService.findById(userId);

        RunningLog runningLog = RunningLog.builder()
                .user(user)
                .runDate(request.getRunDate())
                .duration(toLocalTime(durationMin, durationSec))
                .distance(request.getDistance())
                .memo(request.getMemo())
                .isPublic(request.isPublic())
                .pace(calculatePace(durationMin, durationSec, request.getDistance()))
                .build();

        runningLogRepository.save(runningLog);

        // 이미지가 있으면 각각 저장
        if (images != null && !images.isEmpty()) {
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

        return new RunningLogCreateResponse(runningLog.getRunningLogId());
    }

    // 피드 상세 조회
    @Override
    public FriendFeedResponseDto getFeed(Long runningLogId, Long authorId, Long userId) {
        // 러닝 로그 조회(삭제되지 않은 러닝일지에 한해서)
        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        // authorId가 실제로 해당 runningLogId의 작성자인지 검증
        if (!runningLog.getUser().getUserId().equals(authorId)) {
            throw new BusinessException(ErrorCode.RUNNING_LOG_AUTHOR_MISMATCH);
        }

        // 작성자가 본인이 아닐 경우에 러닝일지 공개 여부 검증
        if (!userId.equals(authorId)){
            if (!runningLog.isPublic()) {
                throw new BusinessException(ErrorCode.PRIVATE_RUNNING_LOG);
            }
        }

        // 파일명을 바탕으로 imageUrl 반환
        List<String> imageUrls = runningLog.getImages().stream()
                .map(image -> imageService.getRunningLogImageUrl(image.getImageUrl()))
                .collect(java.util.stream.Collectors.toList());


        User author = runningLog.getUser();

        // FriendFeedResponseDto 생헝 후 반환
        return new FriendFeedResponseDto(
                runningLog.getRunningLogId(), author.getUserId(), author.getNickName(),
                imageService.getProfileImageUrl(author.getImageUrl()),
                runningLog.getRunDate(), runningLog.getDistance(), runningLog.getPace(),
                runningLog.getDuration(), runningLog.getMemo(), runningLog.getCreatedAt(),
                runningLog.getCommentCtn(), runningLog.getLikeCtn(),
                imageUrls
        );
    }

    @Override
    public void updateRunningLog(Long runningLogId, Long userId, RunningLogUpdateRequest request, List<MultipartFile> images) {

        // 러닝일지 조회 (삭제된 로그 제외)
        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(()-> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        // 유저가 해당 러닝일지의 작성자인지 검증
        validateAuthor(userId, runningLog);

        // 러닝로그 업데이트
        setupRunningLog(runningLog, request);

        // 러닝로그 이미지 업데이트
        setupRunningLogImage(runningLog, images);

    }

    @Override
    public void softDeleteRunningLog(Long runningLogId, Long userId) {
        // RunningLog 조회
        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        // 유저가 작성자인지 검증
        validateAuthor(userId, runningLog);

        // soft 삭제
        runningLog.delete();
    }

    private static void validateAuthor(Long userId, RunningLog runningLog) {
        if (!runningLog.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHORIZATION);
        }
    }

    // 러닝 로그 이미지 업데이트
    public void setupRunningLogImage(RunningLog runningLog, List<MultipartFile> images){
        // 1. 새 이미지가 들어온 경우에만 기존 이미지를 교체
        if (images != null && !images.isEmpty()) {

            // 이미지 개수 제한 체크
            if (images.size() > 5) {
                throw new BusinessException(ErrorCode.TOO_MANY_IMAGES);
            }
            // 1. 기존 이미지 리스트 비우기 (orphanRemoval=true에 의해 DB 데이터도 자동 삭제됨)
            runningLog.getImages().clear();

            // 2. 새 이미지 저장
            for(MultipartFile image : images){

                //파일 저장
                String fileName = imageService.saveRunningLogImage(runningLog.getUser().getUserId(), image);
                RunningLogImage runningLogImage = RunningLogImage.builder()
                        .runningLog(runningLog)
                        .imageUrl(fileName)
                        .build();

                // 연관관계 편의 메서드 사용
                runningLog.addImage(runningLogImage);
            }

        }


        // 2. RunningLogImage 객체 생성
    }
    // 러닝 로그 설정
    public void setupRunningLog(RunningLog runningLog, RunningLogUpdateRequest request) {
        // 분, 초 구하기
        int durationMin = request.getDurationMin() != null ? request.getDurationMin() : 0;
        int durationSec = request.getDurationSec() != null ? request.getDurationSec() : 0;

        // 분, 초 유효성 검사
        if (durationMin == 0 && durationSec == 0) {
            throw new BusinessException(ErrorCode.INVALID_DURATION);
        }
        runningLog.update(
                toLocalTime(durationMin, durationSec),
                request.getRunDate(),
                request.getDistance(),
                request.getMemo(),
                request.isPublic(),
                calculatePace(durationMin, durationSec, request.getDistance())

        );
    };


    private String calculatePace(int durationMin, int durationSec, BigDecimal distance) {
        if (distance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DISTANCE);
        }
        long totalSeconds = (long) durationMin * 60 + durationSec;
        long paceSeconds = Math.round(totalSeconds / distance.doubleValue());
        long min = paceSeconds / 60;
        long sec = paceSeconds % 60;
        return String.format("%d'%02d\"", min, sec);
    }

    private LocalTime toLocalTime(int durationMin, int durationSec) {
        int totalSeconds = durationMin * 60 + durationSec;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return LocalTime.of(hours, minutes, seconds);
    }
}
