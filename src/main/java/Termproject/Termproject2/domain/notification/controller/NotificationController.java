package Termproject.Termproject2.domain.notification.controller;

import Termproject.Termproject2.domain.notification.service.NotificationService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenExtractor jwtTokenExtractor;

    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "알림 리스트를 15개씩 무한 스크롤")
    public ResponseEntity<ApiResponse<?>> getNotificationList(
            @RequestParam(required = false) Long lastNotificationId,
            @RequestParam(defaultValue = "15") int size) {


        Long userId = jwtTokenExtractor.getUserId();

        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotificationList(userId, lastNotificationId, size), "알림 목록을 성공적으로 조회하였습니다."));
    }
}
