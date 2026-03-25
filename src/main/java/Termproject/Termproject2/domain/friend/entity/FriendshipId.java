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

    @Column(name = "recieve_user_id", length = 20)
    private Long receiveUserId;

    @Column(name = "sender_user_id", length = 20)
    private Long senderUserId;

    public FriendshipId(Long receiveUserId, Long senderUserId) {
        this.receiveUserId = receiveUserId;
        this.senderUserId = senderUserId;
    }
}
