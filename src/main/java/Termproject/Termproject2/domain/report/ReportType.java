package Termproject.Termproject2.domain.report;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "REPORT_TYPE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_type_id")
    private Long reportTypeId;

    @Column(name = "type_value", length = 30, nullable = false)
    private String typeValue;

    @Column(name = "type_name", length = 30, nullable = false)
    private String typeName;
}
