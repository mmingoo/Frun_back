package Termproject.Termproject2.domain.report.repository;

import Termproject.Termproject2.domain.report.ReportType;
import Termproject.Termproject2.domain.report.dto.ReportRequestDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportTypeRepository extends JpaRepository<ReportType, Long> {
    ReportType findByTypeName(String typeValue);
}
