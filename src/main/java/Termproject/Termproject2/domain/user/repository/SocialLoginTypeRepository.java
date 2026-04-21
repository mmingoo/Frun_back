package Termproject.Termproject2.domain.user.repository;

import Termproject.Termproject2.domain.user.entity.SocialLoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialLoginTypeRepository extends JpaRepository<SocialLoginType, Long> {
    //TODO: 타입 이름으로 조회
    SocialLoginType findByTypeName(String typeName);
}
