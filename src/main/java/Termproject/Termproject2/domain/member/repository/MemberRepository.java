package Termproject.Termproject2.domain.member.repository;

import Termproject.Termproject2.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUserName(String username);
    boolean existsByNickName(String checkNickname);
}
