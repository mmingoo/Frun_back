package Termproject.Termproject2.domain.user.dto.response;

import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 유저 페이지 정보 응답 DTO
@Getter
@AllArgsConstructor
public class UserPageResponseDto {
    private Long userId; // 유저 ID
    private String nickname; // 닉네임
    private String profileImageUrl; // 프로필 이미지 URL
    private String bio; // 소개글
    private long friendCount; // 친구 수
    private boolean isOwner; // 본인 여부
    private FriendRequestStatus friendRequestStatus; // 친구 관계 상태 (본인이면 null)
    private long totalRunCount; // 총 러닝 횟수
    private double totalDistanceKm; // 총 러닝 거리 (km)
    private String avgPace; // 평균 페이스
}
