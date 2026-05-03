package Termproject.Termproject2.domain.notification.controller;

import Termproject.Termproject2.domain.notification.dto.request.SelectedNotificationRequestDto;
import Termproject.Termproject2.domain.notification.service.NotificationService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * [GET] /api/v1/notification
     * 알림 목록 조회 - 커서 기반 무한 스크롤, 조회 시 읽음 처리
     */
    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "알림 리스트를 15개씩 무한 스크롤")
    public ResponseEntity<ApiResponse<?>> getNotificationList(
            @RequestParam(required = false) Long lastNotificationId,
            @RequestParam(defaultValue = "30") int size
    ) {
        Long userId = JwtTokenExtractor.getUserId();

        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotificationList(userId, lastNotificationId, size), "알림 목록을 성공적으로 조회하였습니다."));
    }

    /**
     * [DELETE] /api/v1/notification/selected-notification
     * 선택한 알림 삭제
     */
    @DeleteMapping("/selected-notification")
    @Operation(summary = "선택한 알림 삭제")
    public ResponseEntity<ApiResponse<?>> deleteSelectedNotificationList(
            @RequestBody SelectedNotificationRequestDto selectedNotificationRequestDto
    ) {
        Long userId = JwtTokenExtractor.getUserId();

        // 선택한 알림 삭제
        notificationService.deleteSelectedNotification(userId, selectedNotificationRequestDto);

        return ResponseEntity.ok(ApiResponse.ok("선택한 알림 삭제하였습니다."));
    }

    /**
     * [DELETE] /api/v1/notification
     * 알림 전체 삭제
     */
    @DeleteMapping
    @Operation(summary = "알림 전체 삭제")
    public ResponseEntity<ApiResponse<?>> deleteAllNotification() {
        Long userId = JwtTokenExtractor.getUserId();
        notificationService.deleteAllNotification(userId);
        return ResponseEntity.ok(ApiResponse.ok("모든 알림을 삭제하였습니다."));
    }
}
