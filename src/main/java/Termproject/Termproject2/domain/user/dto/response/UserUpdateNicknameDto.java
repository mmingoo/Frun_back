package Termproject.Termproject2.domain.user.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 닉네임 변경 요청
public class UserUpdateNicknameDto {
    @NotBlank(message = "닉네임은 필수입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{5,10}$", message = "닉네임은 5~10자의 한글, 영문 대/소문자, 숫자만 사용 가능하며 공백은 허용되지 않습니다.")
    private String nickname; // 변경할 닉네임 (5~10자)

}
