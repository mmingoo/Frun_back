package Termproject.Termproject2.domain.notice.controller;

import Termproject.Termproject2.domain.notice.service.NoticeService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * [GET] /api/v1/notices
     * 공지사항 목록 조회 - 5개씩 커서 기반 무한 스크롤
     */
    @GetMapping
    @Operation(summary = "공지사항 목록 조회", description = "공지사항 제목 목록을 5개씩 커서 기반으로 조회합니다.")
    public ApiResponse<?> getNoticeList(
            @Parameter(description = "이전 페이지 마지막 noticeId (첫 요청 시 생략)") @RequestParam(required = false) Long cursorId) {
        return ApiResponse.ok(noticeService.getNoticeList(cursorId), "공지사항 목록을 성공적으로 조회하였습니다.");
    }

    /**
     * [GET] /api/v1/notices/{noticeId}
     * 공지사항 상세 조회 - 제목, 내용, 작성일시 반환
     */
    @GetMapping("/{noticeId}")
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 ID로 제목, 내용, 작성일시를 조회합니다.")
    public ApiResponse<?> getNoticeDetail(
            @Parameter(description = "조회할 공지사항 ID") @PathVariable Long noticeId) {
        return ApiResponse.ok(noticeService.getNoticeDetail(noticeId), "공지사항을 성공적으로 조회하였습니다.");
    }
}
