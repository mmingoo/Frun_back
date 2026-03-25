package Termproject.Termproject2.domain.friend.controller;

import Termproject.Termproject2.domain.friend.service.FriendShipService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend")
public class FriendController {

    private final JwtTokenExtractor jwtTokenExtractor;
    private final FriendShipService friendShipService;

    @GetMapping("/friend-list")
    @Operation(summary = "친구 목록 조회", description = "현재 친구들의 목록을 조회합니다.")
    public ApiResponse<?> getFriendList(){
        Long userId = jwtTokenExtractor.getUserId();


        return ApiResponse.ok(friendShipService.getFriendList(userId), "성공적으로 친구 목록을 조회하였습니다.");

    }
}
