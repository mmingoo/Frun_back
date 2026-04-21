package Termproject.Termproject2.domain.user.entity;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    DIRECT_INACTIVE,
    REPORT_INACTIVE;

    /**
     * 세 가지 비활성화 상태(INACTIVE, DIRECT_INACTIVE, REPORT_INACTIVE)를 하나로 판단
     * 비활성화 여부를 체크하는 모든 곳에서 이 메서드를 사용한다.
     */
    public boolean isInactive() {
        return this == INACTIVE || this == DIRECT_INACTIVE || this == REPORT_INACTIVE;
    }
}
