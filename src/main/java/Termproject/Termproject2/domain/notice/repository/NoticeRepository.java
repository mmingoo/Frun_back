package Termproject.Termproject2.domain.notice.repository;

import Termproject.Termproject2.domain.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    //TODO: 최신 공지사항 6개 조회 (첫 페이지)
    List<Notice> findTop6ByOrderByNoticeIdDesc();
    //TODO: 커서 이후 공지사항 6개 조회
    List<Notice> findTop6ByNoticeIdLessThanOrderByNoticeIdDesc(Long cursorId);
}
