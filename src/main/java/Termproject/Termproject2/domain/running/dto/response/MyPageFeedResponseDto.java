package Termproject.Termproject2.domain.running.dto.response;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Getter
public class MyPageFeedResponseDto {
    private Long authorId;
    private Long runningLogId;
    private LocalDate runDate;
    private BigDecimal distance;
    private String pace;
    private LocalTime duration;
    private int likeCtn;
    private List<String> logImages;

    // QueryDSL Projections 용 (이미지 제외)
    public MyPageFeedResponseDto(Long authorId, Long runningLogId, LocalDate runDate, BigDecimal distance,
                                  String pace, LocalTime duration, int likeCtn) {
        this.authorId = authorId;
        this.runningLogId = runningLogId;
        this.runDate = runDate;
        this.distance = distance;
        this.pace = pace;
        this.duration = duration;
        this.likeCtn = likeCtn;
        this.logImages = Collections.emptyList();
    }

    // 서비스에서 이미지 포함해 변환 시 사용
    public MyPageFeedResponseDto(Long authorId, Long runningLogId, LocalDate runDate, BigDecimal distance,
                                  String pace, LocalTime duration, int likeCtn,
                                  List<String> logImages) {
        this.authorId = authorId;
        this.runningLogId = runningLogId;
        this.runDate = runDate;
        this.distance = distance;
        this.pace = pace;
        this.duration = duration;
        this.likeCtn = likeCtn;
        this.logImages = logImages;
    }
}
