package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.request.RunningLogCreateRequest;
import Termproject.Termproject2.domain.running.dto.request.RunningLogUpdateRequest;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.dto.response.RunningLogCreateResponse;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RunningLogService {
    RunningLogCreateResponse createRunningLog(Long userId, RunningLogCreateRequest request, List<MultipartFile> images);
    FriendFeedResponseDto getFeed(Long runningLogId, Long authorId, Long userId);

    void updateRunningLog(Long runningLogId, Long userId, @Valid RunningLogUpdateRequest request, List<MultipartFile> newImages);
    void softDeleteRunningLog(Long runningLogId, Long userId);
    RunningLog findById(Long runningLogId);

    boolean existsById(Long runningLogId);
}
