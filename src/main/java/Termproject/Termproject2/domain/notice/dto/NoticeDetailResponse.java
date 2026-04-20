package Termproject.Termproject2.domain.notice.dto;

import Termproject.Termproject2.domain.notice.Notice;
import Termproject.Termproject2.domain.notice.NoticeType;
import lombok.Getter;

import java.time.LocalDateTime;

// 공지사항 상세 응답
@Getter
public class NoticeDetailResponse {

    private final Long noticeId; // 공지사항 ID
    private final String title; // 제목
    private final String content; // 내용
    private final NoticeType noticeType; // 공지 유형
    private final LocalDateTime createdAt; // 작성일시
    private final LocalDateTime updatedAt; // 수정일시

    public NoticeDetailResponse(Notice notice) {
        this.noticeId = notice.getNoticeId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.noticeType = notice.getType();
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
    }
}
