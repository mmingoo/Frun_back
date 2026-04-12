package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.friend.service.FriendShipService;
import Termproject.Termproject2.domain.notification.service.NotificationService;
import Termproject.Termproject2.domain.running.repository.RunningLogRepository;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock private UserRepository userRepository;
    @Mock private RunningLogRepository runningLogRepository;
    @Mock private ImageService imageService;
    @Mock private FriendShipService friendShipService;
    @Mock private NotificationService notificationService;
    @Mock private RefreshTokenService refreshTokenService;

    private User activeUser;

    @BeforeEach
    void setUp() {
        // 기본 활성 유저 생성 (Builder에 userStatus 없으므로 ACTIVE 가 기본값)
        activeUser = User.builder()
                .userName("test_user")
                .name("테스트")
                .userEmail("test@test.com")
                .userPhone("01000000000")
                .build();
        ReflectionTestUtils.setField(activeUser, "userId", 1L);
    }

    // ── 헬퍼: 특정 status 의 User 반환 ─────────────────────────────
    private User userWithStatus(UserStatus status) {
        User user = User.builder()
                .userName("test_user")
                .name("테스트")
                .userEmail("test@test.com")
                .userPhone("01000000000")
                .build();
        ReflectionTestUtils.setField(user, "userId", 1L);
        ReflectionTestUtils.setField(user, "userStatus", status);
        return user;
    }

    private User userWithStatusAndDeactivatedAt(UserStatus status) {
        User user = userWithStatus(status);
        ReflectionTestUtils.setField(user, "deactivatedAt", LocalDateTime.now().minusDays(1));
        return user;
    }

    // ── userDeactivate ──────────────────────────────────────────────

    @Test
    @DisplayName("ACTIVE 유저는 정상적으로 비활성화된다")
    void deactivate_activeUser_success() {
        given(userRepository.findById(1L)).willReturn(Optional.of(activeUser));

        Long result = userService.userDeactivate(1L);

        assertThat(result).isEqualTo(1L);
        assertThat(activeUser.getUserStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("INACTIVE 유저를 비활성화하면 USER_ALREADY_INACTIVE 예외가 발생한다")
    void deactivate_inactiveUser_throwsException() {
        given(userRepository.findById(1L)).willReturn(Optional.of(userWithStatus(UserStatus.INACTIVE)));

        assertThatThrownBy(() -> userService.userDeactivate(1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.USER_ALREADY_INACTIVE));
    }

    @Test
    @DisplayName("DIRECT_INACTIVE 유저를 비활성화하면 USER_ALREADY_INACTIVE 예외가 발생한다")
    void deactivate_directInactiveUser_throwsException() {
        given(userRepository.findById(1L)).willReturn(Optional.of(userWithStatus(UserStatus.DIRECT_INACTIVE)));

        assertThatThrownBy(() -> userService.userDeactivate(1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.USER_ALREADY_INACTIVE));
    }

    @Test
    @DisplayName("REPORT_INACTIVE 유저를 비활성화하면 USER_ALREADY_INACTIVE 예외가 발생한다")
    void deactivate_reportInactiveUser_throwsException() {
        given(userRepository.findById(1L)).willReturn(Optional.of(userWithStatus(UserStatus.REPORT_INACTIVE)));

        assertThatThrownBy(() -> userService.userDeactivate(1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.USER_ALREADY_INACTIVE));
    }

    // ── getInactiveInfo ─────────────────────────────────────────────

    @Test
    @DisplayName("INACTIVE 유저는 비활성 정보를 정상 조회한다")
    void getInactiveInfo_inactiveUser_success() {
        given(userRepository.findById(1L)).willReturn(Optional.of(userWithStatusAndDeactivatedAt(UserStatus.INACTIVE)));

        assertThat(userService.getInactiveInfo(1L)).isNotNull();
    }

    @Test
    @DisplayName("DIRECT_INACTIVE 유저는 비활성 정보를 정상 조회한다")
    void getInactiveInfo_directInactiveUser_success() {
        given(userRepository.findById(1L)).willReturn(Optional.of(userWithStatusAndDeactivatedAt(UserStatus.DIRECT_INACTIVE)));

        assertThat(userService.getInactiveInfo(1L)).isNotNull();
    }

    @Test
    @DisplayName("REPORT_INACTIVE 유저는 비활성 정보를 정상 조회한다")
    void getInactiveInfo_reportInactiveUser_success() {
        given(userRepository.findById(1L)).willReturn(Optional.of(userWithStatusAndDeactivatedAt(UserStatus.REPORT_INACTIVE)));

        assertThat(userService.getInactiveInfo(1L)).isNotNull();
    }

    @Test
    @DisplayName("ACTIVE 유저는 비활성 정보 조회 시 USER_NOT_FOUND 예외가 발생한다")
    void getInactiveInfo_activeUser_throwsException() {
        given(userRepository.findById(1L)).willReturn(Optional.of(activeUser));

        assertThatThrownBy(() -> userService.getInactiveInfo(1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.USER_NOT_FOUND));
    }
}
