package Termproject.Termproject2.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NoticeListResponse {
    private List<NoticeResponseDto> notices;
    private int currentPage; // 현재 페이지
    private int totalPages; // 전체 페이지
    private long totalElements; // 총 개수
    private boolean hasNext; // 페이징 다음 존재 여부
}
