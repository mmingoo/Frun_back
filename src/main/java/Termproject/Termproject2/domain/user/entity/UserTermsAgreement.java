package Termproject.Termproject2.domain.user.entity;
import Termproject.Termproject2.global.common.basedTime.BaseCreatedEntity;
import Termproject.Termproject2.global.common.basedTime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_terms_agreement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTermsAgreement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agreementId; // 동의 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 동의한 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terms_id", nullable = false)
    private Terms terms; // 동의 대상 약관

    @Column(name = "is_agreed", nullable = false)
    private Boolean isAgreed; // 동의 여부


    public static UserTermsAgreement of(User user, Terms terms, Boolean isAgreed) {
        UserTermsAgreement agreement = new UserTermsAgreement();
        agreement.user = user;
        agreement.terms = terms;
        agreement.isAgreed = isAgreed;
        return agreement;
    }

    public void updateAgreement(Boolean isAgreed) {
        this.isAgreed = isAgreed;
    }
}