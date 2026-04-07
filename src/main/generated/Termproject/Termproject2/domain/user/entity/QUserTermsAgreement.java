package Termproject.Termproject2.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserTermsAgreement is a Querydsl query type for UserTermsAgreement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserTermsAgreement extends EntityPathBase<UserTermsAgreement> {

    private static final long serialVersionUID = -1296098536L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserTermsAgreement userTermsAgreement = new QUserTermsAgreement("userTermsAgreement");

    public final Termproject.Termproject2.global.common.basedTime.QBaseCreatedEntity _super = new Termproject.Termproject2.global.common.basedTime.QBaseCreatedEntity(this);

    public final NumberPath<Long> agreementId = createNumber("agreementId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final BooleanPath isAgreed = createBoolean("isAgreed");

    public final QTerms terms;

    public final QUser user;

    public QUserTermsAgreement(String variable) {
        this(UserTermsAgreement.class, forVariable(variable), INITS);
    }

    public QUserTermsAgreement(Path<? extends UserTermsAgreement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserTermsAgreement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserTermsAgreement(PathMetadata metadata, PathInits inits) {
        this(UserTermsAgreement.class, metadata, inits);
    }

    public QUserTermsAgreement(Class<? extends UserTermsAgreement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.terms = inits.isInitialized("terms") ? new QTerms(forProperty("terms")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

