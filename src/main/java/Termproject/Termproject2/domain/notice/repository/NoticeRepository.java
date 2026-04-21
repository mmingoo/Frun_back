package Termproject.Termproject2.domain.notice.repository;

import Termproject.Termproject2.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    //TODO: 내림차순 정렬 모든 공지사항 조회
    Page<Notice> findAllByOrderByNoticeIdDesc(Pageable pageable);
}
