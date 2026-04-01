package Termproject.Termproject2.domain.stats.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRunningStats is a Querydsl query type for RunningStats
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRunningStats extends EntityPathBase<RunningStats> {

    private static final long serialVersionUID = 923832026L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRunningStats runningStats = new QRunningStats("runningStats");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> runCount = createNumber("runCount", Integer.class);

    public final StringPath statKey = createString("statKey");

    public final EnumPath<RunningStats.StatType> statType = createEnum("statType", RunningStats.StatType.class);

    public final NumberPath<Integer> totalDistM = createNumber("totalDistM", Integer.class);

    public final NumberPath<Integer> totalDurSec = createNumber("totalDurSec", Integer.class);

    public final Termproject.Termproject2.domain.user.entity.QUser user;

    public QRunningStats(String variable) {
        this(RunningStats.class, forVariable(variable), INITS);
    }

    public QRunningStats(Path<? extends RunningStats> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRunningStats(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRunningStats(PathMetadata metadata, PathInits inits) {
        this(RunningStats.class, metadata, inits);
    }

    public QRunningStats(Class<? extends RunningStats> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new Termproject.Termproject2.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

