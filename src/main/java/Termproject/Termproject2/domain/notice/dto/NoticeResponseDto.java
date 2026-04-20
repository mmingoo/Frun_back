package Termproject.Termproject2.domain.notice.dto;

import Termproject.Termproject2.domain.notice.Notice;
import Termproject.Termproject2.domain.notice.NoticeType;
import lombok.Getter;

import java.time.LocalDateTime;

// 공지사항 목록용 단건 응답
@Getter
public class NoticeResponseDto {

    private final Long noticeId; // 공지사항 ID
    private final String title; // 제목
    private final NoticeType noticeType; // 공지 유형
    private final LocalDateTime createdDate; // 작성일시

    public NoticeResponseDto(Notice notice) {
        this.noticeId = notice.getNoticeId();
        this.title = notice.getTitle();
        this.noticeType = notice.getType();
        this.createdDate = notice.getCreatedAt();
    }
}
