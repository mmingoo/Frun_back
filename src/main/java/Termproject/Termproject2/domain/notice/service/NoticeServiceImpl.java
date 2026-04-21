package Termproject.Termproject2.domain.notice.service;

import Termproject.Termproject2.domain.notice.entity.Notice;
import Termproject.Termproject2.domain.notice.dto.NoticeDetailResponse;
import Termproject.Termproject2.domain.notice.dto.NoticeListResponse;
import Termproject.Termproject2.domain.notice.dto.NoticeResponseDto;
import Termproject.Termproject2.domain.notice.repository.NoticeRepository;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    // TODO: 공지사항 목록 조회
    @Override
    public NoticeListResponse getNoticeList(int page) {
        // 페이징 처리된 공지사항 목록
        Page<Notice> noticePage = noticeRepository.findAllByOrderByNoticeIdDesc(
                PageRequest.of(page, PAGE_SIZE));

        // 반환 dto 로 변환
        List<NoticeResponseDto> notices = noticePage.getContent().stream()
                .map(NoticeResponseDto::new)
                .collect(Collectors.toList());

        // 페이징 정보도 반영하여 dto 생성
        return new NoticeListResponse(notices, noticePage.getNumber(), noticePage.getTotalPages(),
                noticePage.getTotalElements(), noticePage.hasNext());
    }

    //TODO: 공지사항 상세 조회
    @Override
    public NoticeDetailResponse getNoticeDetail(Long noticeId) {

        // 공지사항 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));

        return new NoticeDetailResponse(notice);
    }
}
