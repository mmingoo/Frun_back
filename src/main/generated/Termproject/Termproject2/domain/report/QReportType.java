package Termproject.Termproject2.domain.report;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReportType is a Querydsl query type for ReportType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReportType extends EntityPathBase<ReportType> {

    private static final long serialVersionUID = 737272294L;

    public static final QReportType reportType = new QReportType("reportType");

    public final NumberPath<Long> reportTypeId = createNumber("reportTypeId", Long.class);

    public final StringPath typeName = createString("typeName");

    public final StringPath typeValue = createString("typeValue");

    public QReportType(String variable) {
        super(ReportType.class, forVariable(variable));
    }

    public QReportType(Path<? extends ReportType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReportType(PathMetadata metadata) {
        super(ReportType.class, metadata);
    }

}

