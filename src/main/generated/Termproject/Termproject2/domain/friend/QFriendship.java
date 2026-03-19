package Termproject.Termproject2.domain.friend;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriendship is a Querydsl query type for Friendship
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriendship extends EntityPathBase<Friendship> {

    private static final long serialVersionUID = 2144226696L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriendship friendship = new QFriendship("friendship");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QFriendshipId id;

    public final Termproject.Termproject2.domain.member.entity.QMember receiveUser;

    public final Termproject.Termproject2.domain.member.entity.QMember senderUser;

    public QFriendship(String variable) {
        this(Friendship.class, forVariable(variable), INITS);
    }

    public QFriendship(Path<? extends Friendship> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriendship(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriendship(PathMetadata metadata, PathInits inits) {
        this(Friendship.class, metadata, inits);
    }

    public QFriendship(Class<? extends Friendship> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QFriendshipId(forProperty("id")) : null;
        this.receiveUser = inits.isInitialized("receiveUser") ? new Termproject.Termproject2.domain.member.entity.QMember(forProperty("receiveUser"), inits.get("receiveUser")) : null;
        this.senderUser = inits.isInitialized("senderUser") ? new Termproject.Termproject2.domain.member.entity.QMember(forProperty("senderUser"), inits.get("senderUser")) : null;
    }

}

