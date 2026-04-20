package Termproject.Termproject2.domain.notice.service;

import Termproject.Termproject2.domain.notice.Notice;
import Termproject.Termproject2.domain.notice.dto.NoticeDetailResponse;
import Termproject.Termproject2.domain.notice.dto.NoticeListResponse;
import Termproject.Termproject2.domain.notice.dto.NoticeResponseDto;
import Termproject.Termproject2.domain.notice.repository.NoticeRepository;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeServiceImpl implements NoticeService {

    private static final int PAGE_SIZE = 5;

    private final NoticeRepository noticeRepository;

    //TODO: 공지사항 목록 커서 기반 조회
    @Override
    public NoticeListResponse getNoticeList(Long cursorId) {
        List<Notice> results = cursorId == null
                ? noticeRepository.findTop6ByOrderByNoticeIdDesc()
                : noticeRepository.findTop6ByNoticeIdLessThanOrderByNoticeIdDesc(cursorId);

        boolean hasNext = results.size() > PAGE_SIZE;
        if (hasNext) {
            results = results.subList(0, PAGE_SIZE);
        }

        List<NoticeResponseDto> notices = results.stream()
                .map(NoticeResponseDto::new)
                .collect(Collectors.toList());

        Long nextCursorId = hasNext ? results.get(results.size() - 1).getNoticeId() : null;
        return new NoticeListResponse(notices, hasNext, nextCursorId);
    }

    //TODO: 공지사항 상세 조회
    @Override
    public NoticeDetailResponse getNoticeDetail(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));
        return new NoticeDetailResponse(notice);
    }
}
