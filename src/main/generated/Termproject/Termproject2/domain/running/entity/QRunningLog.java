package Termproject.Termproject2.domain.running.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRunningLog is a Querydsl query type for RunningLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRunningLog extends EntityPathBase<RunningLog> {

    private static final long serialVersionUID = 943198687L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRunningLog runningLog = new QRunningLog("runningLog");

    public final NumberPath<Integer> commentCtn = createNumber("commentCtn", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> distance = createNumber("distance", java.math.BigDecimal.class);

    public final TimePath<java.time.LocalTime> duration = createTime("duration", java.time.LocalTime.class);

    public final ListPath<RunningLogImage, QRunningLogImage> images = this.<RunningLogImage, QRunningLogImage>createList("images", RunningLogImage.class, QRunningLogImage.class, PathInits.DIRECT2);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final BooleanPath isPublic = createBoolean("isPublic");

    public final NumberPath<Integer> likeCtn = createNumber("likeCtn", Integer.class);

    public final StringPath memo = createString("memo");

    public final StringPath pace = createString("pace");

    public final DatePath<java.time.LocalDate> runDate = createDate("runDate", java.time.LocalDate.class);

    public final NumberPath<Long> runningLogId = createNumber("runningLogId", Long.class);

    public final TimePath<java.time.LocalTime> runTime = createTime("runTime", java.time.LocalTime.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final Termproject.Termproject2.domain.user.entity.QUser user;

    public QRunningLog(String variable) {
        this(RunningLog.class, forVariable(variable), INITS);
    }

    public QRunningLog(Path<? extends RunningLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRunningLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRunningLog(PathMetadata metadata, PathInits inits) {
        this(RunningLog.class, metadata, inits);
    }

    public QRunningLog(Class<? extends RunningLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new Termproject.Termproject2.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

