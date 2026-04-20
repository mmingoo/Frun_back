package Termproject.Termproject2.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
// 약관 동의 변경 요청
public class TermsUpdateRequest {
    private List<TermsUpdateItem> agreements; // 변경할 약관 동의 목록

    @Getter
    @NoArgsConstructor
    public static class TermsUpdateItem {
        private Long termId; // 약관 ID
        private Boolean agreed; // 동의 여부
    }
}
