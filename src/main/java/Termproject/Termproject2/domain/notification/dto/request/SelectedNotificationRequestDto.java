package Termproject.Termproject2.domain.notification.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SelectedNotificationRequestDto {
    List<Long> selectedNotificationIds;


}
