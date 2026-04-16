package Termproject.Termproject2.domain.friend.repository;

import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    Optional<FriendRequest> findByReceiver_UserIdAndSender_UserId(Long receiverUserId, Long senderUserId);

    void deleteBySenderAndReceiver(User sender, User receiver);

    @Modifying
    @Query("DELETE FROM FriendRequest fr WHERE fr.receiver.userId = :userId OR fr.sender.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
