package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.friend.repository.FriendshipRepository;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.user.dto.response.MyPageResponseDto;
import Termproject.Termproject2.domain.user.dto.response.NicknameCheckResponse;
import Termproject.Termproject2.domain.user.dto.response.NicknameStatusResponse;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final RunningLogRepository runningLogRepository;
    private final ImageService imageService;


    // 닉네임 중복 체크
    @Override
    public NicknameCheckResponse nicknameDuplicateCheck(String checkNickname) {
        // 닉네임 존재 여부
        boolean isExists = userRepository.existsByNickName(checkNickname);
        return new NicknameCheckResponse(isExists);
    }

    // 닉네임이 설정돼있는지 아닌지
    @Override
    public NicknameStatusResponse getNicknameStatus(Long userId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 유저의 닉네임 체크 여부
        boolean hasNickname = user.getNickName() != null && !user.getNickName().isBlank();
        return new NicknameStatusResponse(hasNickname);
    }

    // 유저 프로필 업데이트
    @Override
    @Transactional
    public void setupProfile(Long userId, String nickname, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.updateProfile(nickname, imageUrl);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    // 마이페이지의 내 정보(게시물, 친구 수, 총 거리, 평균 페이스 조회)
    @Override
    public MyPageResponseDto getMyPageInfo(Long userId) {

        // 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 친구 수
        long friendCount = friendshipRepository.countByUserId(userId);

        // DB에서 러닝 횟수, 총 거리, 총 소요 시간(초) 한 번에 조회 > 그렇지 않으면 메모리 상에서 너무 많은 연사이 이뤄줘 러닝로그가 많아질 수록, 트래픽이 몰릴수록 부담
        Object[] stats = runningLogRepository.aggregateStatsByUserId(userId);

        //총 러닝일지 수, 운동 횟수
        long totalRunningLogCnt = ((Number) stats[0]).longValue();

        // 총합 거리
        double totalDistanceKm = Math.round(((Number) stats[1]).doubleValue() * 10.0) / 10.0;

        // 총 운동한 거리
        long totalDurationSec = ((Number) stats[2]).longValue();

        String avgPace = null;
        if (totalDistanceKm > 0) {
            // 평균 페이스 계산
            long avgPaceSeconds = Math.round(totalDurationSec / totalDistanceKm);

            // 페이스 포맷팅
            avgPace = String.format("%d'%02d\"", avgPaceSeconds / 60, avgPaceSeconds % 60);
        }

        return new MyPageResponseDto(
                user.getUserId(),
                user.getNickName(),
                imageService.getProfileImageUrl(user.getImageUrl()),
                user.getBio(),
                friendCount,
                totalRunningLogCnt,
                totalDistanceKm,
                avgPace
        );
    }
}
