package Termproject.Termproject2.domain.report;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReport is a Querydsl query type for Report
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReport extends EntityPathBase<Report> {

    private static final long serialVersionUID = 1597441164L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReport report = new QReport("report");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final Termproject.Termproject2.domain.user.entity.QUser reportedUser;

    public final Termproject.Termproject2.domain.user.entity.QUser reporter;

    public final NumberPath<Long> reportId = createNumber("reportId", Long.class);

    public final StringPath reportReason = createString("reportReason");

    public final QReportType reportType;

    public final Termproject.Termproject2.domain.running.entity.QRunningLog runningLog;

    public final StringPath status = createString("status");

    public QReport(String variable) {
        this(Report.class, forVariable(variable), INITS);
    }

    public QReport(Path<? extends Report> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReport(PathMetadata metadata, PathInits inits) {
        this(Report.class, metadata, inits);
    }

    public QReport(Class<? extends Report> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reportedUser = inits.isInitialized("reportedUser") ? new Termproject.Termproject2.domain.user.entity.QUser(forProperty("reportedUser"), inits.get("reportedUser")) : null;
        this.reporter = inits.isInitialized("reporter") ? new Termproject.Termproject2.domain.user.entity.QUser(forProperty("reporter"), inits.get("reporter")) : null;
        this.reportType = inits.isInitialized("reportType") ? new QReportType(forProperty("reportType")) : null;
        this.runningLog = inits.isInitialized("runningLog") ? new Termproject.Termproject2.domain.running.entity.QRunningLog(forProperty("runningLog"), inits.get("runningLog")) : null;
    }

}

