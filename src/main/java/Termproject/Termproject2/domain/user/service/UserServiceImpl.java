package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.friend.repository.FriendshipRepository;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.user.dto.response.*;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final RunningLogRepository runningLogRepository;
    private final ImageService imageService;


    @Override
    public NicknameCheckResponse nicknameDuplicateCheck(String checkNickname) {
        boolean isExists = userRepository.existsByNickName(checkNickname);
        return new NicknameCheckResponse(isExists);
    }

    @Override
    public NicknameStatusResponse getNicknameStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        boolean hasNickname = user.getNickName() != null && !user.getNickName().isBlank();
        return new NicknameStatusResponse(hasNickname);
    }

    /**
     * 최초 프로필 설정 (닉네임 + 이미지)
     * - 닉네임 중복 여부는 비즈니스 규칙이므로 서비스에서 검증
     */
    @Override
    @Transactional
    public void setupProfile(Long userId, String nickname, String imageUrl) {
        // 닉네임 중복 검증 → 어떤 경로로 호출되든 보장됨
        if (userRepository.existsByNickName(nickname)) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setUpProfile(nickname, imageUrl);
    }

    @Override
    public User findById(Long userId) {
        System.out.println("userId : " + userId);
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
    public UserProfileInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(ErrorCode.NOT_FOUND));
        String imageUrl = imageService.getProfileImageUrl(user.getImageUrl());
        return new UserProfileInfoResponse(userId, imageUrl, user.getNickName());
    }

    @Override
    @Transactional
    public void updateUserProfile(Long userId, UserProfileUpdateRequestDto request, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String bio = request.getBio();
        boolean hasBio = bio != null;
        boolean hasImage = profileImage != null && !profileImage.isEmpty();

        if (!hasBio && !hasImage) return;

        String newBio = hasBio ? bio : user.getBio();
        String newImageUrl = user.getImageUrl();

        if (hasImage) {
            newImageUrl = imageService.saveProfileImage(userId, profileImage);
        }

        user.updateProfile(newBio, newImageUrl);
    }

    @Override
    public Page<User> findByNicknameContaining(String keyword, Pageable pageable) {
        return userRepository.findByNickNameContaining(keyword, pageable);
    }

    @Override
    public List<User> findByNicknameContainingWithCursor(String keyword, String cursorName, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        if (cursorName == null || cursorId == null) {
            return userRepository.findByNickNameContainingNoCursor(keyword, pageable);
        }

        return userRepository.findByNickNameContainingWithCursor(keyword, cursorName, cursorId, pageable);
    }
}