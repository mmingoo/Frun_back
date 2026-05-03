package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.friend.service.FriendShipService;
import Termproject.Termproject2.domain.notification.service.NotificationService;
import Termproject.Termproject2.domain.report.entity.ReportStatus;
import Termproject.Termproject2.domain.report.repository.ReportRepository;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.stats.entity.RunningStats;
import Termproject.Termproject2.domain.user.dto.response.*;
import Termproject.Termproject2.domain.user.dto.response.ReportReasonDto;
import Termproject.Termproject2.domain.user.entity.SanctionType;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserSanctionHistory;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import Termproject.Termproject2.domain.user.repository.UserSanctionHistoryRepository;
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
    private final ReportRepository reportRepository;
    private final UserSanctionHistoryRepository userSanctionHistoryRepository;

    //TODO: 닉네임 중복 여부 확인
    @Override
    public NicknameCheckResponse nicknameDuplicateCheck(String checkNickname) {

        // 닉네임 존재 여부
        boolean isExists = userRepository.existsByNickName(checkNickname);

        return new NicknameCheckResponse(isExists);
    }

    //TODO: 닉네임 설정 여부 조회
    @Override
    public NicknameStatusResponse getNicknameStatus(Long userId) {

        // 유저 조회
        User user = findUserById(userId);

        // 닉네임 존재 여부
        boolean hasNickname = user.getNickName() != null && !user.getNickName().isBlank();

        return new NicknameStatusResponse(hasNickname);
    }

    //TODO: 최초 프로필 설정 (닉네임 + 이미지)
    @Override
    @Transactional
    public void setupProfile(Long userId, String nickname, String imageUrl) {

        // 닉네임 중복 검증 → 어떤 경로로 호출되든 보장됨
        if (userRepository.existsByNickName(nickname)) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = findUserById(userId);

        user.setUpProfile(nickname, imageUrl);
    }

    //TODO: userId로 유저 조회
    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    //TODO: 유저 페이지 정보 조회 (친구 관계·통계 포함)
    @Override
    public UserPageResponseDto getUserPageInfo(Long viewerId, Long targetUserId) {
        // 유저 조회
        User user = findUserById(targetUserId);

        long friendCount = friendShipService.getFriendCount(targetUserId); // 친구 수 조회
        boolean isOwner = viewerId.equals(targetUserId); // 자신의 페이지인지 검증

        // 비활성화 계정인 경우 '비활성화 에러' 발생
        validateActiveUser(user);

        // 자신의 페이지가 아닌 경우에만 해당 유저와 관계 반환
        // FriendRequestStatus.FRIEND , FriendRequestStatus.SENDED, FriendRequestStatus.PENDING
        FriendRequestStatus status = resolveFriendStatus(viewerId, targetUserId, isOwner);

        // 러닝통계 집계
        RunningStatsRaw statsRaw = aggregateRunningStats(targetUserId);

        long totalRunCount = statsRaw.runCount();
        double totalDistanceKm = statsRaw.distanceKm();
        String avgPace = formatAvgPace(statsRaw.durationSec(), totalDistanceKm);

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

    // 비활성화 계정인 경우 '비활성화 에러' 발생
    private void validateActiveUser(User user) {
        if (user.getUserStatus().isInactive()) {
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }
    }

    // 자신의 페이지가 아닌 경우에만 해당 유저와 관계 반환
    // FriendRequestStatus.FRIEND , FriendRequestStatus.SENDED, FriendRequestStatus.PENDING
    private FriendRequestStatus resolveFriendStatus(Long viewerId, Long targetUserId, boolean isOwner) {
        return !isOwner ? friendShipService.getStatus(viewerId, targetUserId) : null;
    }

    // 러닝 통계 원시값 집계 (횟수·거리·시간)
    private RunningStatsRaw aggregateRunningStats(Long userId) {
        Object[] stats = runningLogRepository.aggregateStatsByUserId(userId).get(0);
        long runCount = ((Number) stats[0]).longValue();                                        // 러닝 횟수
        double distanceKm = Math.round(((Number) stats[1]).doubleValue() * 10.0) / 10.0;       // 러닝 총 거리(km)
        long durationSec = ((Number) stats[2]).longValue();                                     // 러닝 총 시간(초)
        return new RunningStatsRaw(runCount, distanceKm, durationSec);
    }

    private record RunningStatsRaw(long runCount, double distanceKm, long durationSec) {}

    // 총 시간(초)을 총 거리(km)로 나누어 km당 평균 페이스(초) 계산 후 문자열로 변환 (5'30")
    private String formatAvgPace(long totalDurationSec, double totalDistanceKm) {
        if (totalDistanceKm <= 0) {
            return null;
        }
        long avgPaceSeconds = Math.round(totalDurationSec / totalDistanceKm);
        return String.format("%d'%02d\"", avgPaceSeconds / 60, avgPaceSeconds % 60);
    }

    //TODO: 헤더용 유저 프로필 간략 정보 조회
    @Override
    public UserProfileInfoResponse getUserInfo(Long userId) {
        // 유저 조회
        User user = findUserById(userId);

        // 이미지 경로 변환 후 조회
        String imageUrl = imageService.getProfileImageUrl(user.getImageUrl());

        // 알림 갯수 조회
        long notificationCnt = notificationService.countByUserUserIdAndIsReadFalse(userId);

        return new UserProfileInfoResponse(userId, imageUrl, user.getNickName(), notificationCnt);
    }

    //TODO: 유저 프로필 수정 (소개글·이미지)
    @Override
    @Transactional
    public void updateUserProfile(Long userId, UserProfileUpdateRequestDto request, MultipartFile profileImage) {
        // 유저 조회
        User user = findUserById(userId);

        // 프로필 소개글
        String bio = request.getBio();

        // 업데이트 항목 존재 여부 (소개글, 이미지)
        boolean hasBio = bio != null;
        boolean hasImage = profileImage != null && !profileImage.isEmpty();

        // 변경사항이 없으면 종료
        if (!hasBio && !hasImage) return;

        // 전달된 값이 없으면 기존 엔티티의 값을 유지, 있다면 업데이트
        String newBio = hasBio ? bio : user.getBio();
        String newImageUrl = user.getImageUrl();

        // 새 이미지가 있다면 서버 저장 후 URL 갱신
        if (hasImage) {
            newImageUrl = imageService.saveProfileImage(userId, profileImage);
        }

        // 변경값 업데이트(Dirty Checking)
        user.updateProfile(newBio, newImageUrl);
    }

    //TODO: 계정 비활성화
    @Override
    @Transactional
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

        // 6. 기존 accessToken 즉시 차단 (TTL 15분)
        refreshTokenService.addToBlacklist(userId);

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

        List<ReportReasonDto> reportReasons = null;
        String adminReason = null;

        // 유저가 신고 누적 비활성화일 경우
        if (user.getUserStatus() == UserStatus.REPORT_INACTIVE) {
            // 신고 사유 + 처리 사유 조회
            reportReasons = reportRepository.findReportReasonsWithActionByUserId(userId, ReportStatus.COMPLETED);
            // 관리자 직접 회원 제재인 경우
        } else if (user.getUserStatus() == UserStatus.DIRECT_INACTIVE) {
            List<UserSanctionHistory> sanctions = userSanctionHistoryRepository
                    .findLatestSanction(userId, SanctionType.DIRECT_INACTIVE);
            adminReason = sanctions.isEmpty() ? null : sanctions.get(0).getReason();


        }

        return new InactiveInfoResponse(user.getDeactivatedAt(), user.getDeletionScheduledAt(),
                user.getUserStatus(), reportReasons, adminReason);
    }

    //TODO: 유저 활성화
    @Override
    @Transactional
    public void userActivate(Long userId) {
        // 유저 조회
        User user = findUserById(userId);

        // 활성화 가능 여부 검증
        validateActivatable(user);

        user.setActive();

        // 비활성화 시 등록된 블랙리스트 해제
        refreshTokenService.removeFromBlacklist(userId);
    }

    // 유저가 활성화된 유저가 아닐 것, REPORT_INACTIVE, DIRECT_INACTIVE는 활성화 불가
    private void validateActivatable(User user) {
        if (user.getUserStatus() == UserStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.USER_ALREADY_ACTIVE);
        }
        if (user.getUserStatus() == UserStatus.REPORT_INACTIVE ||
                user.getUserStatus() == UserStatus.DIRECT_INACTIVE) {
            throw new BusinessException(ErrorCode.USER_CANNOT_ACTIVATE);
        }
    }

    //TODO: 닉네임 포함 유저 페이지 조회
    @Override
    public Page<User> findByNicknameContaining(String keyword, Pageable pageable) {
        return userRepository.findByNickNameContaining(keyword, pageable);
    }

    //TODO: 닉네임 포함 유저 커서 기반 조회
    @Override
    public List<User> findByNicknameContainingWithCursor(String keyword, String cursorName, int size) {
        Pageable pageable = PageRequest.of(0, size);

        if (cursorName == null) {
            return userRepository.findByNickNameContainingNoCursor(keyword, pageable);
        }

        return userRepository.findByNickNameContainingWithCursor(keyword, cursorName, pageable);
    }



    //TODO: 닉네임 변경
    @Override
    @Transactional
    public void updateUserNickname(Long userId, UserUpdateNicknameDto request) {

        // 닉네임 존재 여부 검증
        if (userRepository.existsByNickName(request.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
        User user = findUserById(userId);

        // 닉네임 업데이트
        user.updateUserNickname(request.getNickname());
    }


    //TODO: 유저 삭제 (회원 탈퇴)
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserById(userId);

        //refreshToken 삭제
        refreshTokenService.delete(userId);
        userRepository.delete(user);
    }

}