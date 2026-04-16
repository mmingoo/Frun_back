package Termproject.Termproject2.domain.report.repository;

import Termproject.Termproject2.domain.report.entity.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportTypeRepository extends JpaRepository<ReportType, Long> {
    ReportType findByTypeName(String typeValue);
}
