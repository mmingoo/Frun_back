package Termproject.Termproject2.domain.running.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRunningLogImage is a Querydsl query type for RunningLogImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRunningLogImage extends EntityPathBase<RunningLogImage> {

    private static final long serialVersionUID = -1377109316L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRunningLogImage runningLogImage = new QRunningLogImage("runningLogImage");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final NumberPath<Long> logImageId = createNumber("logImageId", Long.class);

    public final QRunningLog runningLog;

    public QRunningLogImage(String variable) {
        this(RunningLogImage.class, forVariable(variable), INITS);
    }

    public QRunningLogImage(Path<? extends RunningLogImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRunningLogImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRunningLogImage(PathMetadata metadata, PathInits inits) {
        this(RunningLogImage.class, metadata, inits);
    }

    public QRunningLogImage(Class<? extends RunningLogImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.runningLog = inits.isInitialized("runningLog") ? new QRunningLog(forProperty("runningLog"), inits.get("runningLog")) : null;
    }

}

