package Termproject.Termproject2.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NoticeListResponse {
    private List<NoticeResponseDto> notices;
    private boolean hasNext;
    private Long nextCursorId;
}
