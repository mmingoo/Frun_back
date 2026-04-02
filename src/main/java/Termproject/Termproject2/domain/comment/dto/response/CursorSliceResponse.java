package Termproject.Termproject2.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

// 댓글과 답글의 무한스크롤을 위한 공통 형식
@Getter
@AllArgsConstructor
public class CursorSliceResponse<T> {
    private List<T> contents;
    private boolean hasNext;
    private Long nextCursor; // null이면 마지막 페이지
}