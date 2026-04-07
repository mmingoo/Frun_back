package Termproject.Termproject2.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTerms is a Querydsl query type for Terms
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTerms extends EntityPathBase<Terms> {

    private static final long serialVersionUID = -1516463331L;

    public static final QTerms terms = new QTerms("terms");

    public final Termproject.Termproject2.global.common.basedTime.QBaseCreatedEntity _super = new Termproject.Termproject2.global.common.basedTime.QBaseCreatedEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final BooleanPath isRequired = createBoolean("isRequired");

    public final NumberPath<Long> termsId = createNumber("termsId", Long.class);

    public final StringPath termsType = createString("termsType");

    public final StringPath title = createString("title");

    public QTerms(String variable) {
        super(Terms.class, forVariable(variable));
    }

    public QTerms(Path<? extends Terms> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTerms(PathMetadata metadata) {
        super(Terms.class, metadata);
    }

}

