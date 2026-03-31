package Termproject.Termproject2.domain.running.service;

import Termproject.Termproject2.domain.running.entity.Like;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.repository.LikeRepository;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RunningLogRepository runningLogRepository;


    // 좋아요 하기
    @Override
    @Transactional
    public void addLike(Long userId, Long runningLogId) {

        // 좋아요를 여러번 하는 경우 에러 처리
        if (likeRepository.existsByUserUserIdAndRunningLogRunningLogId(userId, runningLogId)) {
            throw new BusinessException(ErrorCode.ALREADY_LIKED);
        }

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 러닝로그 조회
        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        // 좋아요 생성 후 저장
        likeRepository.save(
                Like.builder()
                        .user(user)
                        .runningLog(runningLog)
                        .build());


        // 러닝일지에 좋아요 cnt + 1
        runningLog.addLikeCnt();
    }


    // 좋아요 취소
    @Override
    @Transactional
    public void removeLike(Long userId, Long runningLogId) {

        // 좋아요 취소 반복 요청하는 경우 에러 처리
        if (!likeRepository.existsByUserUserIdAndRunningLogRunningLogId(userId, runningLogId)) {
            throw new BusinessException(ErrorCode.LIKE_NOT_FOUND);
        }

        // 좋아요 데이터 삭제
        likeRepository.deleteByUserUserIdAndRunningLogRunningLogId(userId, runningLogId);


        // 러닝로그 조회
        RunningLog runningLog = runningLogRepository.findByRunningLogIdAndIsDeletedFalse(runningLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUNNING_LOG_NOT_FOUND));

        // 러닝일지에 좋아요 cnt - 1
        runningLog.minusLikeCnt();

    }
}
