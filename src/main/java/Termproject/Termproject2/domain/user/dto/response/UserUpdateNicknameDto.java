package Termproject.Termproject2.domain.user.dto.response;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateNicknameDto {
    @Size(max = 20, message = "닉네임 20글자까지만 사용하실 수 있습니다.")
    private String nickname;

}
