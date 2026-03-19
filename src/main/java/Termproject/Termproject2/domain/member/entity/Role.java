package Termproject.Termproject2.domain.member.entity;

public enum Role {
    USER("USER", "유저"), ADMIN("admin", "관리자");
    private final String code;
    private final String description;

    // 생성자
    Role(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // Getter
    public String getCode() { return code; }
    public String getDescription() { return description; }
}
