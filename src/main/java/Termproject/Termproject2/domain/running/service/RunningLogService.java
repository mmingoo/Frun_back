package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.dto.request.RunningLogCreateRequest;
import Termproject.Termproject2.domain.running.dto.response.RunningLogCreateResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RunningLogService {
    RunningLogCreateResponse createRunningLog(Long userId, RunningLogCreateRequest request, List<MultipartFile> images);
}
