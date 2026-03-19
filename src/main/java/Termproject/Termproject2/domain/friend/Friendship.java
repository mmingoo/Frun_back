package Termproject.Termproject2.domain.friend;

import Termproject.Termproject2.domain.member.entity.Member;
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
public class Friendship {

    @EmbeddedId
    private FriendshipId id;

    @MapsId("receiveUserId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recieve_user_id", referencedColumnName = "user_id")
    private Member receiveUser;

    @MapsId("senderUserId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id", referencedColumnName = "user_id")
    private Member senderUser;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Friendship(Member receiveUser, Member senderUser) {
        this.receiveUser = receiveUser;
        this.senderUser = senderUser;
        this.id = new FriendshipId(receiveUser.getUserId(), senderUser.getUserId());
    }
}
