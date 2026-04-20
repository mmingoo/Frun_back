package Termproject.Termproject2.domain.user.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 닉네임 중복 여부 응답
public class NicknameCheckResponse {
    boolean isExists; // 닉네임 중복 여부


}
