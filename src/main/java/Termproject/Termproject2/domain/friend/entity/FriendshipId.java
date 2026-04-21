package Termproject.Termproject2.domain.friend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class FriendshipId implements Serializable {

    // 받은 유저의 id
    @Column(name = "recieve_user_id", length = 20)
    private Long receiveUserId;

    // 보낸 유저의 id
    @Column(name = "sender_user_id", length = 20)
    private Long senderUserId;

    public FriendshipId(Long receiveUserId, Long senderUserId) {
        this.receiveUserId = receiveUserId;
        this.senderUserId = senderUserId;
    }
}
