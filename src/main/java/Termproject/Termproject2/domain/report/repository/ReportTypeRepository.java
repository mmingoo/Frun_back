package Termproject.Termproject2.domain.report.repository;

import Termproject.Termproject2.domain.report.entity.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportTypeRepository extends JpaRepository<ReportType, Long> {
    //TODO: 유형명으로 신고 유형 조회
    ReportType findByTypeName(String typeValue);
}
