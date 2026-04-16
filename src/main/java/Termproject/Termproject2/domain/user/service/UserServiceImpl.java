package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.friend.service.FriendShipService;
import Termproject.Termproject2.domain.notification.service.NotificationService;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.user.dto.response.*;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RunningLogRepository runningLogRepository;
    private final ImageService imageService;
    private final FriendShipService friendShipService;
    private final NotificationService notificationService;
    private final RefreshTokenService refreshTokenService;

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
        FriendRequestStatus status = null;
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        long friendCount = friendShipService.getFriendCount(targetUserId);

        boolean isOwner = viewerId.equals(targetUserId);

        if (!isOwner && user.getUserStatus().isInactive()) {
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }

        if(!isOwner){
            status =  friendShipService.getStatus(viewerId,targetUserId);
        }

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
                status,
                totalRunCount,
                totalDistanceKm,
                avgPace
        );
    }

    @Override
    public UserProfileInfoResponse getUserInfo(Long userId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(ErrorCode.NOT_FOUND));

        // 이미지 경로 변환 후 조회
        String imageUrl = imageService.getProfileImageUrl(user.getImageUrl());

        // 알림 갯수 조회
        long notificationCnt = notificationService.countByUserUserIdAndIsReadFalse(userId);
        return new UserProfileInfoResponse(userId, imageUrl, user.getNickName(), notificationCnt);
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

    //Todo: 계정 비활성화
    @Override
    @Transactional // 데이터 변경이 일어나므로 트랜잭션 보장이 필수입니다.
    public Long userDeactivate(Long userId) {

        // 1. 유저 조회
        User user = findUserById(userId);

        // 2. 이미 비활성화 상태인지 확인 (INACTIVE / DIRECT_INACTIVE / REPORT_INACTIVE 모두 포함)
        if (user.getUserStatus().isInactive()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_INACTIVE);
        }

        // 3. 상태 변경 (활성화 시점, 비활성화 시점, 삭제시점)
        user.setInActive();

        // 5. Redis의 refreshToken 즉시 무효화
        refreshTokenService.delete(userId);

        // 6. 처리된 유저의 ID 반환
        return user.getUserId();
    }

    //TODO: 비활성화된 계정 정보 조회
    @Override
    public InactiveInfoResponse getInactiveInfo(Long userId) {
        User user = findUserById(userId);
        // INACTIVE / DIRECT_INACTIVE / REPORT_INACTIVE 모두 비활성 정보 조회 허용
        // DIRECT_INACTIVE / REPORT_INACTIVE 는 기존 데이터에서 deactivatedAt 이 null 일 수 있으므로 상태만 체크
        if (!user.getUserStatus().isInactive()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return new InactiveInfoResponse(user.getDeactivatedAt(), user.getDeletionScheduledAt(), user.getUserStatus());
    }

    //TODO: 유저 활성화
    @Override
    @Transactional
    public void userActivate(Long userId) {
        User user = findUserById(userId);

        if (user.getUserStatus() == UserStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.USER_ALREADY_ACTIVE);
        }

        user.setActive();
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



    //TODO: 닉네임 변경
    @Override
    @Transactional
    public void updateUserNickname(Long userId, UserUpdateNicknameDto request) {
        User user = findUserById(userId);
        user.updateUserNickname(request.getNickname()) ;
    }


    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        refreshTokenService.delete(userId);
        userRepository.delete(user);
    }

    //TODO: USER 반환 메서드, 에러 처리
    private User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}