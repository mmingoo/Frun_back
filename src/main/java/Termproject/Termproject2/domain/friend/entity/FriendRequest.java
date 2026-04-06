package Termproject.Termproject2.domain.friend.entity;

import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.global.common.basedTime.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "FRIEND_REQUEST")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequest extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_request_id")
    private Long friendRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private FriendRequestStatus status;

    @Builder
    public FriendRequest(User receiver, User sender, FriendRequestStatus status) {
        this.receiver = receiver;
        this.sender = sender;
        this.status = (status != null) ? status : FriendRequestStatus.SENDED;
        // status가 null로 들어올 경우 기본값으로 PENDING 설정
    }

    public void setStatus(FriendRequestStatus friendRequestStatus){
        this.status = friendRequestStatus;
    }



}
