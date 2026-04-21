package Termproject.Termproject2.domain.notice.entity;

import Termproject.Termproject2.global.common.basedTime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NOTICE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId; // 공지사항 ID

    @Column(name = "title", length = 300, nullable = false)
    private String title; // 제목

    @Column(name = "content", length = 2000, nullable = false)
    private String content; // 내용

    @Enumerated(EnumType.STRING)
    @Column(name = "notice_type", length = 15, nullable = false)
    private NoticeType type; // 공지 유형

    @Builder
    public Notice(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
    }
}
