package Termproject.Termproject2.domain.notice.entity;

import Termproject.Termproject2.global.common.basedTime.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NOTICE_IMAGE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeImage extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_image_id")
    private Long id;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Builder
    public NoticeImage(Notice notice, String imageUrl) {
        this.notice = notice;
        this.imageUrl = imageUrl;
    }
}
