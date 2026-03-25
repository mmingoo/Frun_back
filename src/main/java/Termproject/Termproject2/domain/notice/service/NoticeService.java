package Termproject.Termproject2.domain.notice.service;

import Termproject.Termproject2.domain.notice.dto.NoticeListResponse;

public interface NoticeService {
    NoticeListResponse getNoticeList(Long cursorId);
}
