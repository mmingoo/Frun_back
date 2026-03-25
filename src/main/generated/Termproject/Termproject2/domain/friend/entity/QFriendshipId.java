package Termproject.Termproject2.domain.friend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFriendshipId is a Querydsl query type for FriendshipId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QFriendshipId extends BeanPath<FriendshipId> {

    private static final long serialVersionUID = -1875330798L;

    public static final QFriendshipId friendshipId = new QFriendshipId("friendshipId");

    public final NumberPath<Long> receiveUserId = createNumber("receiveUserId", Long.class);

    public final NumberPath<Long> senderUserId = createNumber("senderUserId", Long.class);

    public QFriendshipId(String variable) {
        super(FriendshipId.class, forVariable(variable));
    }

    public QFriendshipId(Path<? extends FriendshipId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFriendshipId(PathMetadata metadata) {
        super(FriendshipId.class, metadata);
    }

}

