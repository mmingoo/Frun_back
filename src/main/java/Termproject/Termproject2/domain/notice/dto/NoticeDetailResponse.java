package Termproject.Termproject2.domain.notice.dto;

import Termproject.Termproject2.domain.notice.entity.Notice;
import Termproject.Termproject2.domain.notice.entity.NoticeType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// 공지사항 상세 응답
@Getter
public class NoticeDetailResponse {

    private final Long noticeId;
    private final String title;
    private final String content;
    private final NoticeType noticeType;
    private final LocalDateTime createdAt;
    private final List<String> imageUrls;

    public NoticeDetailResponse(Notice notice, List<String> imageUrls) {
        this.noticeId = notice.getNoticeId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.noticeType = notice.getType();
        this.createdAt = notice.getCreatedAt();
        this.imageUrls = imageUrls;
    }
}
