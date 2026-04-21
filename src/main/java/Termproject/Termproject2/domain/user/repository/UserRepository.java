package Termproject.Termproject2.domain.user.repository;

import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //TODO: 소셜 로그인 식별자로 유저 조회
    User findByUserName(String username);

    //TODO: 닉네임 중복 여부 확인
    boolean existsByNickName(String checkNickname);

    //TODO: userId로 프로필 이미지 경로만 조회
    @Query("SELECT u.imageUrl FROM User u WHERE u.userId = :userId")
    String findImageUrlByUserId(@Param(value = "userId") Long userId);

    //TODO: 닉네임 포함 유저 페이지 조회
    Page<User> findByNickNameContaining(String nickName, Pageable page);

    //TODO: 닉네임 포함 유저 커서 기반 조회 (닉네임 오름차순)
    @Query("SELECT u FROM User u WHERE u.nickName LIKE %:keyword% AND (u.nickName > :cursorName OR (u.nickName = :cursorName AND u.userId > :cursorId)) ORDER BY u.nickName ASC, u.userId ASC")
    List<User> findByNickNameContainingWithCursor(@Param("keyword") String keyword, @Param("cursorName") String cursorName, @Param("cursorId") Long cursorId, Pageable pageable);

    //TODO: 닉네임 포함 유저 조회 (커서 없이 첫 페이지)
    @Query("SELECT u FROM User u WHERE u.nickName LIKE %:keyword% ORDER BY u.nickName ASC, u.userId ASC")
    List<User> findByNickNameContainingNoCursor(@Param("keyword") String keyword, Pageable pageable);

    //TODO: 상태·삭제 예정일로 유저 조회
    List<User> findAllByUserStatusAndDeletionScheduledAtBefore(UserStatus userStatus, LocalDateTime now);

}
