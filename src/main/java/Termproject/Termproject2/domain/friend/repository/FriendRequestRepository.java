package Termproject.Termproject2.domain.friend.repository;

import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    //TODO: 수신자·발신자 ID로 친구 요청 조회
    Optional<FriendRequest> findByReceiver_UserIdAndSender_UserId(Long receiverUserId, Long senderUserId);

    //TODO: 특정 유저와 여러 상대방 간의 친구 요청 일괄 조회
    @Query("SELECT fr FROM FriendRequest fr WHERE (fr.sender.userId = :me AND fr.receiver.userId IN :targetIds) OR (fr.receiver.userId = :me AND fr.sender.userId IN :targetIds)")
    List<FriendRequest> findAllByMeAndTargetIds(@Param("me") Long me, @Param("targetIds") List<Long> targetIds);

    //TODO: 발신자·수신자로 친구 요청 삭제
    void deleteBySenderAndReceiver(User sender, User receiver);

    //TODO: 유저와 관련된 모든 친구 요청 삭제 (회원 탈퇴 시)
    @Modifying
    @Query("DELETE FROM FriendRequest fr WHERE fr.receiver.userId = :userId OR fr.sender.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
