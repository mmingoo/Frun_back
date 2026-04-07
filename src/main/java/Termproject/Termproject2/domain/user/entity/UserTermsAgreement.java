package Termproject.Termproject2.domain.user.entity;
import Termproject.Termproject2.global.common.basedTime.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_terms_agreement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTermsAgreement extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agreementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terms_id", nullable = false)
    private Terms terms;

    @Column(nullable = false)
    private Boolean isAgreed;


    public static UserTermsAgreement of(User user, Terms terms, Boolean isAgreed) {
        UserTermsAgreement agreement = new UserTermsAgreement();
        agreement.user = user;
        agreement.terms = terms;
        agreement.isAgreed = isAgreed;
        return agreement;
    }
}