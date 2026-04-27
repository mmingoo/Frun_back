package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.request.RunningLogCreateRequest;
import Termproject.Termproject2.domain.running.dto.request.RunningLogUpdateRequest;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.RunningLogCreateResponse;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface RunningLogService {
    //TODO: 러닝 일지 생성
    RunningLogCreateResponse createRunningLog(Long userId, RunningLogCreateRequest request, List<MultipartFile> images);

    //TODO: 러닝로그 상세 조회 (공개 여부·활성 계정 검증 포함)
    FriendFeedResponseDto getFeed(Long runningLogId, Long userId);

    //TODO: 러닝 일지 수정
    void updateRunningLog(Long runningLogId, Long userId, @Valid RunningLogUpdateRequest request, List<MultipartFile> newImages);

    //TODO: 러닝 일지 soft 삭제
    void softDeleteRunningLog(Long runningLogId, Long userId);

    //TODO: runningLogId로 러닝일지 조회
    RunningLog findById(Long runningLogId);

    //TODO: 러닝일지 존재 여부 확인
    boolean existsById(Long runningLogId);

    //TODO: 삭제되지 않은 공개 러닝일지 조회
    RunningLog findByNotDeletedAndPublicRunningLog (Long runningLogId);

    // TODO: 특정 범위에 해당하는 삭제되지 않은 공개 러닝일지 찾기
    List<RunningLog> findRunningLofBetweenDate(Long userId, LocalDate weekStart, LocalDate weekEnd);
}
