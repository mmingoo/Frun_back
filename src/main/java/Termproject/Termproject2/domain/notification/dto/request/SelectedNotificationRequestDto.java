package Termproject.Termproject2.domain.notification.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
// 선택 알림 삭제 요청
public class SelectedNotificationRequestDto {
    List<Long> selectedNotificationIds; // 삭제할 알림 ID 목록


}
