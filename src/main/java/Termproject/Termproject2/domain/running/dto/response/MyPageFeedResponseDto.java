package Termproject.Termproject2.domain.running.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

// 마이페이지 러닝일지 피드 단건 응답
@Getter
public class MyPageFeedResponseDto {
    private Long authorId;
    private Long runningLogId;
    private LocalDate runDate;
    private BigDecimal distance;
    private String pace;
    private LocalTime duration;
    private int likeCtn;
    private int commentCtn;
    private String memo;
    @Setter
    private String thumbnailImage;
    private LocalTime runTime;
    private Integer paceSeconds;

    // QueryDSL Projections 용
    public MyPageFeedResponseDto(Long authorId, Long runningLogId, LocalDate runDate, BigDecimal distance,
                                  String pace, LocalTime duration, int likeCtn, int commentCtn, String memo,
                                  LocalTime runTime, Integer paceSeconds) {
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
        this.runTime = runTime;
        this.paceSeconds = paceSeconds;
    }
}
