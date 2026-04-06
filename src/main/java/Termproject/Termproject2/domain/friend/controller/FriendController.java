package Termproject.Termproject2.domain.friend.controller;

import Termproject.Termproject2.domain.friend.dto.response.UserSearchListResponse;
import Termproject.Termproject2.domain.friend.dto.request.FriendRequestDto;
import Termproject.Termproject2.domain.friend.service.FriendShipService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            @Parameter(description = "한 번에 조회할 수 (기본값 20)") @RequestParam(defaultValue = "5") int size) {
        System.out.println("친구 목록 조회");
        System.out.println("커서네임 : " + cursorName);
        System.out.println("커서아이디 : " + cursorId);
        Long userId = jwtTokenExtractor.getUserId();
        return ApiResponse.ok(friendShipService.getFriendList(userId, cursorName, cursorId, size), "성공적으로 친구 목록을 조회하였습니다.");
    }

    @GetMapping("/search")
    @Operation(summary = "친구 검색", description = "닉네임 오름차순 커서 기반 무한 스크롤로 유저를 검색합니다.")
    public ResponseEntity<ApiResponse<UserSearchListResponse>> search(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "이전 페이지 마지막 유저 닉네임 (첫 요청 시 생략)") @RequestParam(required = false) String cursorName,
            @Parameter(description = "이전 페이지 마지막 userId (cursorName과 함께 사용)") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 번에 조회할 수 (기본값 20)") @RequestParam(defaultValue = "20") int size) {

        Long userId = jwtTokenExtractor.getUserId();
        return ResponseEntity.ok(ApiResponse.ok(
                friendShipService.searchUsersWithDetailStatus(userId, keyword, cursorName, cursorId, size),
                "성공적으로 유저를 검색하였습니다."
        ));
    }



    @PostMapping("/request/{friendId}")
    @Operation(summary = "친구 요청")
    public ResponseEntity<ApiResponse<?>> sendRequest(
            @PathVariable Long friendId
    ) {
        Long userId = jwtTokenExtractor.getUserId();
        friendShipService.sendFriendRequest(userId, friendId);
        return ResponseEntity.ok(ApiResponse.ok("친구 요청을 보냈습니다."));
    }


    @PostMapping("/request/accept/{senderId}")
    @Operation(summary = "친구 요청 수락")
    public ResponseEntity<ApiResponse<?>> acceptRequest(@PathVariable Long senderId) {
        Long userId = jwtTokenExtractor.getUserId();
        friendShipService.acceptFriendRequest(senderId,userId);
        return ResponseEntity.ok(ApiResponse.ok("친구 요청을 수락했습니다."));
    }

    @DeleteMapping("/request/reject/{senderId}")
    @Operation(summary = "친구 요청 거절")
    public ResponseEntity<ApiResponse<?>> rejectRequest(@PathVariable Long senderId) {
        Long userId = jwtTokenExtractor.getUserId();
        friendShipService.rejectFriendRequest(senderId, userId);
        return ResponseEntity.ok(ApiResponse.ok("친구 요청을 거절했습니다."));
    }

    @DeleteMapping("/{friendId}")
    @Operation(summary = "친구를 삭제.")
    public ResponseEntity<ApiResponse<Void>> deleteFriend(
            @PathVariable Long friendId) {

        Long myId = jwtTokenExtractor.getUserId();

        friendShipService.unfriend(myId, friendId);

        return ResponseEntity.ok(ApiResponse.ok("친구 삭제가 완료되었습니다."));
    }
}
