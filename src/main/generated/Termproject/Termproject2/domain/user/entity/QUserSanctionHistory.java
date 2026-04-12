package Termproject.Termproject2.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserSanctionHistory is a Querydsl query type for UserSanctionHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserSanctionHistory extends EntityPathBase<UserSanctionHistory> {

    private static final long serialVersionUID = -1997776312L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserSanctionHistory userSanctionHistory = new QUserSanctionHistory("userSanctionHistory");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath reason = createString("reason");

    public final NumberPath<Long> sanctionId = createNumber("sanctionId", Long.class);

    public final EnumPath<SanctionType> sanctionType = createEnum("sanctionType", SanctionType.class);

    public final QUser targetUser;

    public QUserSanctionHistory(String variable) {
        this(UserSanctionHistory.class, forVariable(variable), INITS);
    }

    public QUserSanctionHistory(Path<? extends UserSanctionHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserSanctionHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserSanctionHistory(PathMetadata metadata, PathInits inits) {
        this(UserSanctionHistory.class, metadata, inits);
    }

    public QUserSanctionHistory(Class<? extends UserSanctionHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.targetUser = inits.isInitialized("targetUser") ? new QUser(forProperty("targetUser"), inits.get("targetUser")) : null;
    }

}

