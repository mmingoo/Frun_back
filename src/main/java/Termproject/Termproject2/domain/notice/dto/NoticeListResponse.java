package Termproject.Termproject2.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 공지사항 목록 커서 기반 응답
public class NoticeListResponse {
    private List<NoticeResponseDto> notices; // 공지사항 목록
    private boolean hasNext; // 다음 페이지 존재 여부
    private Long nextCursorId; // 다음 커서 ID
}
