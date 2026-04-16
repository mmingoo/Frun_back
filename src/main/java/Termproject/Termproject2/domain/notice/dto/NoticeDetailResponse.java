package Termproject.Termproject2.domain.notice.dto;

import Termproject.Termproject2.domain.notice.Notice;
import Termproject.Termproject2.domain.notice.NoticeType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeDetailResponse {

    private final Long noticeId;
    private final String title;
    private final String content;
    private final NoticeType noticeType;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public NoticeDetailResponse(Notice notice) {
        this.noticeId = notice.getNoticeId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.noticeType = notice.getType();
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
    }
}
