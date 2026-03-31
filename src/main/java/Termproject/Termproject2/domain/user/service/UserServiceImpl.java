package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.friend.repository.FriendshipRepository;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.user.dto.response.*;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Override
    public UserPageResponseDto getUserPageInfo(Long viewerId, Long targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        long friendCount = friendshipRepository.countByUserId(targetUserId);

        boolean isOwner = viewerId.equals(targetUserId);
        boolean isFriend = !isOwner &&
                friendshipRepository.findByUserIdAndAuthorId(viewerId, targetUserId).isPresent();

        Object[] stats = runningLogRepository.aggregateStatsByUserId(targetUserId).get(0);

        long totalRunCount = ((Number) stats[0]).longValue();
        double totalDistanceKm = Math.round(((Number) stats[1]).doubleValue() * 10.0) / 10.0;
        long totalDurationSec = ((Number) stats[2]).longValue();

        String avgPace = null;
        if (totalDistanceKm > 0) {
            long avgPaceSeconds = Math.round(totalDurationSec / totalDistanceKm);
            avgPace = String.format("%d'%02d\"", avgPaceSeconds / 60, avgPaceSeconds % 60);
        }

        return new UserPageResponseDto(
                user.getUserId(),
                user.getNickName(),
                imageService.getProfileImageUrl(user.getImageUrl()),
                user.getBio(),
                friendCount,
                isOwner,
                isFriend,
                totalRunCount,
                totalDistanceKm,
                avgPace
        );
    }

    @Override
    public UserProfileInfoResponse getUserInfo(Long userId){
        String imageUrl = userRepository.findImageUrlByUserId(userId);
        return new UserProfileInfoResponse(userId, imageUrl);
    }


    @Override
    @Transactional
    public void updateUserProfile(Long userId, UserProfileUpdateRequestDto request, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String bio = request.getBio();
        boolean hasBio = bio != null;
        boolean hasImage = profileImage != null && !profileImage.isEmpty();

        // 둘 다 null이면 업데이트 x
        if (!hasBio && !hasImage) return;

        String newBio = hasBio ? bio : user.getBio();  // bio 없으면 기존 값 유지
        String newImageUrl = user.getImageUrl(); // 이미지 없으면 기존 값 유지

        // 이미지가 있으면 저장
        if (hasImage) {
            newImageUrl = imageService.saveProfileImage(userId, profileImage);
        }


        user.updateProfile(newBio, newImageUrl);
    }
}

