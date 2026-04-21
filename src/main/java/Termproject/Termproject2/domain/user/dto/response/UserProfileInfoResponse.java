package Termproject.Termproject2.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 헤더용 유저 프로필 간략 정보 응답
public class UserProfileInfoResponse {
    private Long userId;
    private String profileImageUrl; // 프로필 이미지 URL
    private String nickName;
    private long notificationCnt; // 미읽음 알림 수
}
