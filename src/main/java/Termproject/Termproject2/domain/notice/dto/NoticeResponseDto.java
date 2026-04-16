package Termproject.Termproject2.domain.notice.dto;

import Termproject.Termproject2.domain.notice.Notice;
import Termproject.Termproject2.domain.notice.NoticeType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeResponseDto {

    private final Long noticeId;
    private final String title;
    private final NoticeType noticeType;
    private final LocalDateTime createdDate;

    public NoticeResponseDto(Notice notice) {
        this.noticeId = notice.getNoticeId();
        this.title = notice.getTitle();
        this.noticeType = notice.getType();
        this.createdDate = notice.getCreatedAt();
    }
}
