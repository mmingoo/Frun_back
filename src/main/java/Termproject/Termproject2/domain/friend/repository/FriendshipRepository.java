package Termproject.Termproject2.domain.friend.repository;

import Termproject.Termproject2.domain.friend.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface FriendshipRepository extends JpaRepository<Friendship, Long>, FriendshipRepositoryCustom {

    //TODO: 유저의 친구 수 조회
    @Query("SELECT COUNT(f) FROM Friendship f WHERE f.id.senderUserId = :userId OR f.id.receiveUserId = :userId")
    long countByUserId(@Param("userId") Long userId);

    //TODO: 수신자·발신자 ID로 친구 관계 조회
    Optional<Friendship> findByIdReceiveUserIdAndIdSenderUserId(Long receiveUserId, Long senderUserId);

    //TODO: 발신자·수신자 ID로 친구 관계 삭제
    void deleteByIdSenderUserIdAndIdReceiveUserId(Long senderUserId, Long receiveUserId);

    //TODO: 유저의 모든 친구 관계 삭제 (회원 탈퇴 시)
    @Modifying
    @Query("DELETE FROM Friendship f WHERE f.id.receiveUserId = :userId OR f.id.senderUserId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);


    //TODO: 두 유저 간 친구 관계 조회
    @Query("SELECT f FROM Friendship f " +
            "WHERE (f.receiveUser.userId = :userId AND f.senderUser.userId = :authorId) " +
            "OR (f.senderUser.userId = :userId AND f.receiveUser.userId = :authorId)")
    Optional<Friendship> findByUserIdAndAuthorId(@Param("userId") Long userId,
                                                 @Param("authorId") Long authorId);

    //TODO: 친구관계 삭제
    @Modifying
    @Query("DELETE FROM Friendship f " +
            "WHERE (f.id.receiveUserId = :myId AND f.id.senderUserId = :friendId) " +
            "OR (f.id.receiveUserId = :friendId AND f.id.senderUserId = :myId)")
    long deleteFriendship(@Param("myId") Long myId, @Param("friendId") Long friendId);
}
