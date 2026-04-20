package Termproject.Termproject2.domain.notice.service;

import Termproject.Termproject2.domain.notice.dto.NoticeDetailResponse;
import Termproject.Termproject2.domain.notice.dto.NoticeListResponse;

public interface NoticeService {
    //TODO: 공지사항 목록 커서 기반 조회
    NoticeListResponse getNoticeList(Long cursorId);

    //TODO: 공지사항 상세 조회
    NoticeDetailResponse getNoticeDetail(Long noticeId);
}
