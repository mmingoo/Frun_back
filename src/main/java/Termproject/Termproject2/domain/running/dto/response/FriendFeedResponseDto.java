package Termproject.Termproject2.domain.running.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class FriendFeedResponseDto {
    private Long runningLogId;
    private Long userId;
    private String nickName;
    private String imageUrl;
    private LocalDate runDate;
    private BigDecimal distance;
    private String pace;
    private LocalTime duration;
    private String memo;
    private LocalDateTime createdAt;
    private int commentCtn;
    private int likeCtn;
}