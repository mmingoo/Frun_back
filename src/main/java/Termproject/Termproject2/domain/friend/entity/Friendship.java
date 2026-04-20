package Termproject.Termproject2.domain.friend.entity;

import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.global.common.basedTime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "FRIENDSHIP")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship extends BaseTimeEntity {

    @EmbeddedId
    private FriendshipId id; // 복합키 (수신자ID, 발신자ID)

    @MapsId("receiveUserId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recieve_user_id", referencedColumnName = "user_id")
    private User receiveUser; // 친구 요청 수락한 유저

    @MapsId("senderUserId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id", referencedColumnName = "user_id")
    private User senderUser; // 친구 요청 보낸 유저

    @Builder
    public Friendship(User receiveUser, User senderUser) {
        this.receiveUser = receiveUser;
        this.senderUser = senderUser;
        this.id = new FriendshipId(receiveUser.getUserId(), senderUser.getUserId());
    }
}
