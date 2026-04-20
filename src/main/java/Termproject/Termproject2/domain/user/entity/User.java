package Termproject.Termproject2.domain.user.entity;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.friend.entity.Friendship;
import Termproject.Termproject2.domain.notification.entity.Notification;
import Termproject.Termproject2.domain.report.entity.Report;
import Termproject.Termproject2.domain.running.entity.Like;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.stats.entity.RunningStats;
import Termproject.Termproject2.global.common.basedTime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", length = 20, nullable = false)
    private Long userId; // 유저 ID

    @Column(name = "name", length = 20, nullable = false)
    private String name; // 실명

    @Column(name = "user_name", length = 200, nullable = false)
    private String userName; // 소셜 로그인 식별자 (socialType + providerId 조합)

    @Column(name = "user_email", length = 50, nullable = false)
    private String userEmail; // 이메일

    @Column(name = "user_phone", length = 15, nullable = false)
    private String userPhone; // 전화번호

    @Column(name = "provider_id", length = 255)
    private String providerId; // 소셜 제공자 ID

    @Column(name = "nick_name", length = 20)
    private String nickName; // 닉네임 (5~20자, 최초 설정 전 null)

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", length = 20, nullable = false)
    private UserStatus userStatus; // 계정 상태 (ACTIVE / INACTIVE 등)

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10, nullable = false)
    private Role role; // 권한 (USER / ADMIN)

    @Column(name = "image_url", length = 500)
    private String imageUrl; // 프로필 이미지 파일명

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt; // 비활성화 시각

    @Column(name = "deletion_scheduled_at")
    private LocalDateTime deletionScheduledAt; // 물리 삭제 예정일 (비활성화 후 3개월)

    @Column(name = "bio", length = 50)
    private String bio; // 프로필 소개글 (최대 50자)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_type_id")
    private SocialLoginType socialLoginType;

    // ── 계정 삭제 시 cascade 대상 ────────────────────────────────

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> receivedNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> sentNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendRequest> receivedFriendRequests = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendRequest> sentFriendRequests = new ArrayList<>();

    @OneToMany(mappedBy = "receiveUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> receivedFriendships = new ArrayList<>();

    @OneToMany(mappedBy = "senderUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> sentFriendships = new ArrayList<>();

    @OneToMany(mappedBy = "reportedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> receivedReports = new ArrayList<>();

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> sentReports = new ArrayList<>();

    @OneToMany(mappedBy = "targetUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSanctionHistory> sanctionHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunningStats> runningStats = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunningLog> runningLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTermsAgreement> termsAgreements = new ArrayList<>();



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
        this.deactivatedAt = LocalDateTime.now();
        this.deletionScheduledAt = this.deactivatedAt.plusMonths(3);
    }

    // 관리자에 의한 강제 비활성화 (deactivatedAt, deletionScheduledAt 동일하게 설정)
    public void setDirectInactive() {
        this.userStatus = UserStatus.DIRECT_INACTIVE;
        this.deactivatedAt = LocalDateTime.now();
        this.deletionScheduledAt = this.deactivatedAt.plusMonths(3);
    }

    // 신고에 의한 비활성화 (deactivatedAt, deletionScheduledAt 동일하게 설정)
    public void setReportInactive() {
        this.userStatus = UserStatus.REPORT_INACTIVE;
        this.deactivatedAt = LocalDateTime.now();
        this.deletionScheduledAt = this.deactivatedAt.plusMonths(3);
    }

    public void setActive(){
        this.userStatus = UserStatus.ACTIVE;
        this.deactivatedAt = null;
        this.deletionScheduledAt = null;
    }

    public void updateUserNickname(String nickName){
        this.nickName = nickName;
    }



}
