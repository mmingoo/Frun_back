package Termproject.Termproject2.domain.user.repository;

import Termproject.Termproject2.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String username);
    boolean existsByNickName(String checkNickname);


    @Query("SELECT u.imageUrl FROM User u WHERE u.userId = :userId")
    String findImageUrlByUserId(@Param(value = "userId") Long userId);

    Page<User> findByNickNameContaining(String nickName, Pageable page);
}
