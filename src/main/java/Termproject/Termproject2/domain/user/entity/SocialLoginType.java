package Termproject.Termproject2.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SOCIAL_LOGIN_TYPE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialLoginType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_type_id")
    private Long socialTypeId; // 소셜로그인 타입 ID

    @Column(name = "type_value", length = 30, nullable = false)
    private String typeValue; // 소셜로그인 값

    @Column(name = "type_name", length = 30, nullable = false)
    private String typeName; // 소셜로그인 이름
}
