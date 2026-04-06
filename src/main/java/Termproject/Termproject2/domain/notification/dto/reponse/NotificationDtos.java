package Termproject.Termproject2.domain.notification.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NotificationDtos {
    List<NotificationDto> notificationDtoList;
    Long nextCursorId;
    boolean hasNext;
}
