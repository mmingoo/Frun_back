package Termproject.Termproject2.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserStatusTest {

    @Test
    @DisplayName("INACTIVE 는 isInactive() = true")
    void inactive_isInactive() {
        assertThat(UserStatus.INACTIVE.isInactive()).isTrue();
    }

    @Test
    @DisplayName("DIRECT_INACTIVE 는 isInactive() = true")
    void directInactive_isInactive() {
        assertThat(UserStatus.DIRECT_INACTIVE.isInactive()).isTrue();
    }

    @Test
    @DisplayName("REPORT_INACTIVE 는 isInactive() = true")
    void reportInactive_isInactive() {
        assertThat(UserStatus.REPORT_INACTIVE.isInactive()).isTrue();
    }

    @Test
    @DisplayName("ACTIVE 는 isInactive() = false")
    void active_isNotInactive() {
        assertThat(UserStatus.ACTIVE.isInactive()).isFalse();
    }
}
