package Termproject.Termproject2.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 마이페이지 기본 정보 응답
public class MyPageResponseDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl; // 프로필 이미지 URL
    private String bio; // 한 줄 소개
    private long friendCount; // 친구 수
    private long totalRunCount; // 총 러닝 횟수
    private double totalDistanceKm; // 총 러닝 거리 (km)
    private String avgPace; // 평균 페이스
}
