package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.request.RunningLogCreateRequest;
import Termproject.Termproject2.domain.running.dto.response.RunningLogCreateResponse;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.entity.RunningLogImage;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.service.UserService;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RunningLogServiceImpl implements RunningLogService {
    private final UserService userService;
    private final RunningLogRepository runningLogRepository;
    private final ImageService imageService;


    @Override
    public RunningLogCreateResponse createRunningLog(Long userId, RunningLogCreateRequest request, List<MultipartFile> images) {

        //유저 조회
        User user = userService.findById(userId);

        // RunningLong 생성
        RunningLog runningLog = RunningLog.builder()
                .user(user)
                .runDate(request.getRunDate())
                .duration(toLocalTime(request.getDurationMin(), request.getDurationSec()))
                .distance(request.getDistance())
                .memo(request.getMemo())
                .isPublic(request.isPublic())
                .pace(calculatePace(request.getDurationMin(), request.getDurationSec(), request.getDistance()))
                .build();

        // RunningLong 저장
        runningLogRepository.save(runningLog);

        // 이미지가 있으면 업로드 후 RunningLogImage 저장;
        if (images != null && !images.isEmpty()) {

            // 러닝로그에 업로드 된 여러 사진들 저장
            for (MultipartFile image : images) {

                // 저장한 이미지의 url
                String imageUrl = imageService.uploadRunningLogImage(userId, image);
                runningLog.getImages().add(
                        RunningLogImage.builder()
                                .runningLog(runningLog)
                                .imageUrl(imageUrl)
                                .build()
                );
            }
        }
        return new RunningLogCreateResponse(runningLog.getRunningLogId());

    }

    // 페이스 계산
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

    //   DurationMin, DurationSec 를 LocalTime 형식으로 변환
    private LocalTime toLocalTime(int durationMin, int durationSec) {
        int totalSeconds = durationMin * 60 + durationSec;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return LocalTime.of(hours, minutes, seconds);
    }
}
