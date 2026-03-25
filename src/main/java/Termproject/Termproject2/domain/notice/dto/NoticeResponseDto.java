package Termproject.Termproject2.domain.notice.dto;

import Termproject.Termproject2.domain.notice.Notice;
import lombok.Getter;

@Getter
public class NoticeResponseDto {

    private final Long noticeId;
    private final String title;

    public NoticeResponseDto(Notice notice) {
        this.noticeId = notice.getNoticeId();
        this.title = notice.getTitle();
    }
}
