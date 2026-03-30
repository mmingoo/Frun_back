package Termproject.Termproject2.domain.friend.repository;

import Termproject.Termproject2.domain.friend.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long>, FriendshipRepositoryCustom {

    // 조건이 정적이므로 JPQL 사용
    @Query("SELECT COUNT(f) FROM Friendship f WHERE f.id.senderUserId = :userId OR f.id.receiveUserId = :userId")
    long countByUserId(@Param("userId") Long userId);
}
