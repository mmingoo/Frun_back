package Termproject.Termproject2.domain.member.entity;

public enum UserStatus {
    ACTIVE("active", "활성화"), INACTIVE("inactive", "비활성화");
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
}
