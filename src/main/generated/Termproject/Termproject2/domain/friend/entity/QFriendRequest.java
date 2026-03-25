package Termproject.Termproject2.domain.friend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriendRequest is a Querydsl query type for FriendRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriendRequest extends EntityPathBase<FriendRequest> {

    private static final long serialVersionUID = -1601853132L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriendRequest friendRequest = new QFriendRequest("friendRequest");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> friendRequestId = createNumber("friendRequestId", Long.class);

    public final Termproject.Termproject2.domain.user.entity.QUser receiver;

    public final Termproject.Termproject2.domain.user.entity.QUser sender;

    public final EnumPath<FriendRequestStatus> status = createEnum("status", FriendRequestStatus.class);

    public QFriendRequest(String variable) {
        this(FriendRequest.class, forVariable(variable), INITS);
    }

    public QFriendRequest(Path<? extends FriendRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriendRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriendRequest(PathMetadata metadata, PathInits inits) {
        this(FriendRequest.class, metadata, inits);
    }

    public QFriendRequest(Class<? extends FriendRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.receiver = inits.isInitialized("receiver") ? new Termproject.Termproject2.domain.user.entity.QUser(forProperty("receiver"), inits.get("receiver")) : null;
        this.sender = inits.isInitialized("sender") ? new Termproject.Termproject2.domain.user.entity.QUser(forProperty("sender"), inits.get("sender")) : null;
    }

}

