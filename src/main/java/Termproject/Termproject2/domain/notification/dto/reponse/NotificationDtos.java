package Termproject.Termproject2.domain.notification.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 알림 목록 커서 기반 응답
public class NotificationDtos {
    List<NotificationDto> notificationDtoList; // 알림 목록
    Long nextCursorId; // 다음 커서 ID
    boolean hasNext; // 다음 페이지 존재 여부
}
