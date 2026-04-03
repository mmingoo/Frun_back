package Termproject.Termproject2.domain.user.entity;

import Termproject.Termproject2.global.common.basedTime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", length = 20, nullable = false)
    private Long userId;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    // 속성으로 둘까 고민하였지만, 추루 구글 소셜로그인 기능 추가했을 때 providerId가 겹칠 가능성이 있어 socialType + providerId 로 정의함
    @Column(name = "user_name", length = 200, nullable = false)
    private String userName;

    @Column(name = "user_email", length = 200, nullable = false)
    private String userEmail;

    @Column(name = "user_phone", length = 200, nullable = false)
    private String userPhone;

    // ── FRun_SQL USER 추가 컬럼 ─────────────────────────────────
    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "nick_name", length = 20)
    private String nickName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", length = 20, nullable = false)
    private UserStatus userStatus;


    // 권한 제어를 위한 role 추가
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10, nullable = false)
    private Role role;

    @Column(name = "image_url", length = 500)
    private String imageUrl;


    // 프로필 소개글
    @Column(name = "bio", length = 50)
    private String bio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_type_id")
    private SocialLoginType socialLoginType;



    @Builder
    public User(String userName, String userEmail,
                String userPhone, String providerId, String name,
                String nickName, String imageUrl, SocialLoginType socialLoginType, Role role) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.providerId = providerId;
        this.nickName = nickName;
        this.imageUrl = imageUrl;
        this.socialLoginType = socialLoginType;
        this.userStatus = UserStatus.ACTIVE;
        this.name = name;
        this.userName = userName;
        this.role = role;
    }

    public void setUpProfile(String nickName, String imageUrl) {
        if (nickName != null) this.nickName= nickName;
        if (imageUrl != null) this.imageUrl = imageUrl;
    }
    public void updateProfile(String bio, String imageUrl) {
        if (bio != null) this.bio = bio;
        if (imageUrl != null) this.imageUrl = imageUrl;
    }

    public void setInActive(){
        this.userStatus = UserStatus.INACTIVE;
    }
    public void setActive(){
        this.userStatus = UserStatus.ACTIVE;
    }



}
