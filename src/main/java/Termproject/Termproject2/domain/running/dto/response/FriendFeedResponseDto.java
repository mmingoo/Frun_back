package Termproject.Termproject2.domain.running.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

// 친구 피드 응답 DTO (피드 목록 및 상세 조회에 공통 사용)
@Getter
@Builder
public class FriendFeedResponseDto {
    private Long runningLogId; // 러닝일지 ID
    private Long userId; // 작성자 ID
    private String nickName; // 작성자 닉네임
    private String imageUrl; // 작성자 프로필 이미지 URL
    private LocalDate runDate; // 러닝 날짜
    private LocalTime runTime; // 러닝 시작 시간
    private BigDecimal distance; // 거리 (km)
    private String pace; // 평균 페이스
    private LocalTime duration; // 소요 시간
    private String memo; // 메모
    private LocalDateTime createdAt; // 작성일시
    private int likeCtn; // 좋아요 수
    private boolean liked; // 내 좋아요 여부
    private List<String> logImages; // 러닝일지 이미지 URL 목록
    private boolean isPublic; // 공개 여부

    // QueryDSL Projections 용 (이미지 제외, liked는 서비스 레이어에서 설정)
    public FriendFeedResponseDto(Long runningLogId, Long userId, String nickName, String imageUrl,
                                  LocalDate runDate, LocalTime runTime, BigDecimal distance, String pace, LocalTime duration,
                                  String memo, LocalDateTime createdAt, int likeCtn) {
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
        this.likeCtn = likeCtn;
        this.logImages = Collections.emptyList();
    }

    // 서비스에서 이미지 포함해 변환 시 사용
    public FriendFeedResponseDto(Long runningLogId, Long userId, String nickName, String imageUrl,
                                  LocalDate runDate,LocalTime runTime, BigDecimal distance, String pace, LocalTime duration,
                                  String memo, LocalDateTime createdAt, int commentCtn, int likeCtn,
                                 boolean liked, List<String> logImages, boolean isPublic) {
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
        this.likeCtn = likeCtn;
        this.liked = liked;
        this.logImages = logImages;
        this.isPublic = isPublic;
    }

    public void setLiked(boolean liked){
        this.liked = liked;
    }
}