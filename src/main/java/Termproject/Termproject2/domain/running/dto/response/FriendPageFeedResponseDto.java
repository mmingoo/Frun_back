package Termproject.Termproject2.domain.running.dto.response;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

// 친구 마이페이지 러닝일지 피드 단건 응답
@Getter
public class FriendPageFeedResponseDto {
    private Long authorId; // 작성자 ID
    private Long runningLogId; // 러닝일지 ID
    private LocalDate runDate; // 러닝 날짜
    private BigDecimal distance; // 거리 (km)
    private String pace; // 페이스 (mm:ss)
    private LocalTime duration; // 러닝 시간
    private int likeCtn; // 좋아요 수
    private int commentCtn; // 댓글 수
    private String memo; // 메모
    private String thumbnailImage; // 썸네일 이미지 URL

    // QueryDSL Projections 용 (이미지 제외)
    public FriendPageFeedResponseDto(Long authorId, Long runningLogId, LocalDate runDate, BigDecimal distance,
                                 String pace, LocalTime duration, int likeCtn, int commentCtn, String memo) {
        this.authorId = authorId;
        this.runningLogId = runningLogId;
        this.runDate = runDate;
        this.distance = distance;
        this.pace = pace;
        this.duration = duration;
        this.likeCtn = likeCtn;
        this.commentCtn = commentCtn;
        this.memo = memo;
        this.thumbnailImage = null;
    }

    // 서비스에서 썸네일 이미지 포함해 변환 시 사용
    public FriendPageFeedResponseDto(Long authorId, Long runningLogId, LocalDate runDate, BigDecimal distance,
                                 String pace, LocalTime duration, int likeCtn, int commentCtn, String memo,
                                 String thumbnailImage) {
        this.authorId = authorId;
        this.runningLogId = runningLogId;
        this.runDate = runDate;
        this.distance = distance;
        this.pace = pace;
        this.duration = duration;
        this.likeCtn = likeCtn;
        this.commentCtn = commentCtn;
        this.memo = memo;
        this.thumbnailImage = thumbnailImage;
    }
}
