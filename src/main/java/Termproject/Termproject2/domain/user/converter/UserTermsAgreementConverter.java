package Termproject.Termproject2.domain.user.converter;

import Termproject.Termproject2.domain.user.entity.Terms;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserTermsAgreement;

public class UserTermsAgreementConverter {

    public static UserTermsAgreement toUserTermsAgreement(User user, Terms terms, Boolean isAgreed) {
        return UserTermsAgreement.builder()
                .user(user)
                .terms(terms)
                .isAgreed(isAgreed)
                .build();
    }
}
