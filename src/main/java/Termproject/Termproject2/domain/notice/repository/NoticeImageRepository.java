package Termproject.Termproject2.domain.notice.repository;

import Termproject.Termproject2.domain.notice.entity.NoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {

    List<NoticeImage> findByNoticeNoticeIdOrderByIdAsc(Long noticeId);
}
