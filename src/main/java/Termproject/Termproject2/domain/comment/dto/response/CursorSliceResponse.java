package Termproject.Termproject2.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

// 댓글과 답글의 무한스크롤을 위한 공통 형식
@Getter
@AllArgsConstructor
public class CursorSliceResponse<T> {
    private List<T> contents; // 댓글 내용들
    private boolean hasNext; // 다음 댓글이 있는지 여부
    private Long nextCursor; // null이면 마지막 페이지, 아니라면 다음 댓글 시작할 댓글 id
    private long totalCount; // 전체 댓글(또는 답글) 수
}