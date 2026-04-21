package Termproject.Termproject2.domain.notice.dto;

import Termproject.Termproject2.domain.notice.entity.Notice;
import Termproject.Termproject2.domain.notice.entity.NoticeType;
import lombok.Getter;

import java.time.LocalDateTime;

// 공지사항 상세 응답
@Getter
public class NoticeDetailResponse {

    private final Long noticeId;
    private final String title;
    private final String content;
    private final NoticeType noticeType; // 공지 유형
    private final LocalDateTime createdAt;

    public NoticeDetailResponse(Notice notice) {
        this.noticeId = notice.getNoticeId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.noticeType = notice.getType();
        this.createdAt = notice.getCreatedAt();
    }
}
