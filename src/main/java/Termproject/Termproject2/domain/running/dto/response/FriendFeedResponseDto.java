package Termproject.Termproject2.domain.running.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class FriendFeedResponseDto {
    private Long runningLogId;
    private Long userId;
    private String nickName;
    private String imageUrl;
    private LocalDate runDate;
    private LocalTime runTime;
    private BigDecimal distance;
    private String pace;
    private LocalTime duration;
    private String memo;
    private LocalDateTime createdAt;
    private int commentCtn;
    private int likeCtn;
    private List<String> logImages;

    // QueryDSL Projections 용 (이미지 제외, liked는 서비스 레이어에서 설정)
    public FriendFeedResponseDto(Long runningLogId, Long userId, String nickName, String imageUrl,
                                  LocalDate runDate, LocalTime runTime, BigDecimal distance, String pace, LocalTime duration,
                                  String memo, LocalDateTime createdAt, int commentCtn, int likeCtn) {
        this.runningLogId = runningLogId;
        this.userId = userId;
        this.nickName = nickName;
        this.imageUrl = imageUrl;
        this.runDate = runDate;
        this.runTime = runTime;
        this.distance = distance;
        this.pace = pace;
        this.duration = duration;
        this.memo = memo;
        this.createdAt = createdAt;
        this.commentCtn = commentCtn;
        this.likeCtn = likeCtn;
        this.logImages = Collections.emptyList();
    }

    // 서비스에서 이미지 포함해 변환 시 사용
    public FriendFeedResponseDto(Long runningLogId, Long userId, String nickName, String imageUrl,
                                  LocalDate runDate,LocalTime runTime, BigDecimal distance, String pace, LocalTime duration,
                                  String memo, LocalDateTime createdAt, int commentCtn, int likeCtn,
                                  List<String> logImages) {
        this.runningLogId = runningLogId;
        this.userId = userId;
        this.nickName = nickName;
        this.imageUrl = imageUrl;
        this.runDate = runDate;
        this.runTime = runTime;
        this.distance = distance;
        this.pace = pace;
        this.duration = duration;
        this.memo = memo;
        this.createdAt = createdAt;
        this.commentCtn = commentCtn;
        this.likeCtn = likeCtn;
        this.logImages = logImages;
    }
}