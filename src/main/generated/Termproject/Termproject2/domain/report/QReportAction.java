package Termproject.Termproject2.domain.report;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReportAction is a Querydsl query type for ReportAction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReportAction extends EntityPathBase<ReportAction> {

    private static final long serialVersionUID = -715074078L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReportAction reportAction = new QReportAction("reportAction");

    public final StringPath actionReason = createString("actionReason");

    public final StringPath actionType = createString("actionType");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QReport report;

    public final NumberPath<Long> reportId = createNumber("reportId", Long.class);

    public QReportAction(String variable) {
        this(ReportAction.class, forVariable(variable), INITS);
    }

    public QReportAction(Path<? extends ReportAction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReportAction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReportAction(PathMetadata metadata, PathInits inits) {
        this(ReportAction.class, metadata, inits);
    }

    public QReportAction(Class<? extends ReportAction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.report = inits.isInitialized("report") ? new QReport(forProperty("report"), inits.get("report")) : null;
    }

}

