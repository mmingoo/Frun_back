package Termproject.Termproject2.domain.friend.controller;

import Termproject.Termproject2.domain.friend.service.FriendShipService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend")
public class FriendController {

    private final JwtTokenExtractor jwtTokenExtractor;
    private final FriendShipService friendShipService;

    @GetMapping("/friend-list")
    @Operation(summary = "친구 목록 조회", description = "닉네임 오름차순 커서 기반 무한 스크롤로 친구 목록을 조회합니다.")
    public ApiResponse<?> getFriendList(
            @Parameter(description = "이전 페이지 마지막 친구 닉네임 (첫 요청 시 생략)") @RequestParam(required = false) String cursorName,
            @Parameter(description = "이전 페이지 마지막 friendId (cursorName과 함께 사용)") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 번에 조회할 수 (기본값 20)") @RequestParam(defaultValue = "20") int size) {
        Long userId = jwtTokenExtractor.getUserId();
        return ApiResponse.ok(friendShipService.getFriendList(userId, cursorName, cursorId, size), "성공적으로 친구 목록을 조회하였습니다.");
    }
}
