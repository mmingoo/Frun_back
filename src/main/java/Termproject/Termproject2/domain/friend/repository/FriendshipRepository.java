package Termproject.Termproject2.domain.friend.repository;

import Termproject.Termproject2.domain.friend.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long>, FriendshipRepositoryCustom {
}
