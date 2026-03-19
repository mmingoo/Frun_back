package Termproject.Termproject2.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSocialLoginType is a Querydsl query type for SocialLoginType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSocialLoginType extends EntityPathBase<SocialLoginType> {

    private static final long serialVersionUID = 1938090045L;

    public static final QSocialLoginType socialLoginType = new QSocialLoginType("socialLoginType");

    public final NumberPath<Long> socialTypeId = createNumber("socialTypeId", Long.class);

    public final StringPath typeName = createString("typeName");

    public final StringPath typeValue = createString("typeValue");

    public QSocialLoginType(String variable) {
        super(SocialLoginType.class, forVariable(variable));
    }

    public QSocialLoginType(Path<? extends SocialLoginType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSocialLoginType(PathMetadata metadata) {
        super(SocialLoginType.class, metadata);
    }

}

