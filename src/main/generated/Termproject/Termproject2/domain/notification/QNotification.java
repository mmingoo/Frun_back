package Termproject.Termproject2.domain.notification;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = 336567660L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotification notification = new QNotification("notification");

    public final Termproject.Termproject2.domain.comment.QComment comment;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final Termproject.Termproject2.domain.friend.QFriendRequest friendRequest;

    public final BooleanPath isRead = createBoolean("isRead");

    public final NumberPath<Long> notificationId = createNumber("notificationId", Long.class);

    public final EnumPath<NotificationType> type = createEnum("type", NotificationType.class);

    public final Termproject.Termproject2.domain.member.entity.QMember user;

    public QNotification(String variable) {
        this(Notification.class, forVariable(variable), INITS);
    }

    public QNotification(Path<? extends Notification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotification(PathMetadata metadata, PathInits inits) {
        this(Notification.class, metadata, inits);
    }

    public QNotification(Class<? extends Notification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.comment = inits.isInitialized("comment") ? new Termproject.Termproject2.domain.comment.QComment(forProperty("comment"), inits.get("comment")) : null;
        this.friendRequest = inits.isInitialized("friendRequest") ? new Termproject.Termproject2.domain.friend.QFriendRequest(forProperty("friendRequest"), inits.get("friendRequest")) : null;
        this.user = inits.isInitialized("user") ? new Termproject.Termproject2.domain.member.entity.QMember(forProperty("user"), inits.get("user")) : null;
    }

}

