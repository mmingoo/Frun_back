package Termproject.Termproject2.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 닉네임 설정 여부 응답
public class NicknameStatusResponse {
    private boolean hasNickname; // 닉네임 존재 여부
}
