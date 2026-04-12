package Termproject.Termproject2.domain.user.entity;

public enum UserStatus {
    ACTIVE("active", "활성화"),
    INACTIVE("inactive", "비활성화"),
    DIRECT_INACTIVE("directInactive", "관리자 비활성화"),
    REPORT_INACTIVE("reportInactive", "신고로 인한 비활성화");



    private final String code;
    private final String description;

    // 생성자
    UserStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // Getter
    public String getCode() { return code; }
    public String getDescription() { return description; }

    /**
     * 세 가지 비활성화 상태(INACTIVE, DIRECT_INACTIVE, REPORT_INACTIVE)를 하나로 판단
     * 비활성화 여부를 체크하는 모든 곳에서 이 메서드를 사용한다.
     */
    public boolean isInactive() {
        return this == INACTIVE || this == DIRECT_INACTIVE || this == REPORT_INACTIVE;
    }
}
