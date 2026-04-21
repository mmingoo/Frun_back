package Termproject.Termproject2.domain.notification.repository;

import Termproject.Termproject2.domain.notification.dto.reponse.NotificationDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationRepositoryCustom {
    //TODO: 유저의 알림 목록 커서 기반 조회 (발신자, 러닝일지, 댓글 정보 포함)
    List<NotificationDto> findNotificationByUserUserId(Long userId, Long lastId, Pageable pageable);
}
