package Termproject.Termproject2.domain.user.dto.response;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 프로필 소개글 수정 요청
public class UserProfileUpdateRequestDto {
    @Size(max = 50, message = "소개글은 50자 까지 작성할 수 있습니다.")
    private String bio; // 소개글 (최대 50자)

}
