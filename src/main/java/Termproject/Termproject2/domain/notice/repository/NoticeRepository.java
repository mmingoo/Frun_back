package Termproject.Termproject2.domain.notice.repository;

import Termproject.Termproject2.domain.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findTop6ByOrderByNoticeIdDesc();
    List<Notice> findTop6ByNoticeIdLessThanOrderByNoticeIdDesc(Long cursorId);
}
