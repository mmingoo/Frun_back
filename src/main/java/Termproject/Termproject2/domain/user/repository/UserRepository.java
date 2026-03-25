package Termproject.Termproject2.domain.user.repository;

import Termproject.Termproject2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String username);
    boolean existsByNickName(String checkNickname);
}
