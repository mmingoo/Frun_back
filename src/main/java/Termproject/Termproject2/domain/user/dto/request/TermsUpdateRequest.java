package Termproject.Termproject2.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TermsUpdateRequest {
    private List<TermsUpdateItem> agreements;

    @Getter
    @NoArgsConstructor
    public static class TermsUpdateItem {
        private Long termId;
        private Boolean agreed;
    }
}
