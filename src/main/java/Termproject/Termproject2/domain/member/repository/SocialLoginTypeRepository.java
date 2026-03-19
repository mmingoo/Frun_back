// Repository - 이름을 SocialLoginTypeRepository로 변경
package Termproject.Termproject2.domain.member.repository;

import Termproject.Termproject2.domain.member.entity.SocialLoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialLoginTypeRepository extends JpaRepository<SocialLoginType, Long> {
    SocialLoginType findByTypeName(String typeName);
}