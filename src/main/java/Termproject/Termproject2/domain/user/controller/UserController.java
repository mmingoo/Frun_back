package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.domain.user.dto.response.UserProfileUpdateRequestDto;
import Termproject.Termproject2.domain.user.service.UserService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.image.ImageService;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ImageService imageService;

    /**
     * [GET] /api/v1/users/{userId}/mypage
     * 유저 페이지 정보 조회 - 친구 수, 러닝 통계, 친구 관계 상태 포함
     */
    @GetMapping("/{userId}/mypage")
    @Operation(summary = "유저 페이지 정보 조회", description = "유저의 페이지 정보를 조회합니다. 본인이면 isOwner=true, isFriend=false.")
    public ApiResponse<?> getUserPage(@PathVariable Long userId) {
        Long viewerId = JwtTokenExtractor.getUserId();
        return ApiResponse.ok(userService.getUserPageInfo(viewerId, userId), "조회되었습니다.");
    }

    /**
     * [GET] /api/v1/users/me
     * 내 정보 조회 - 프로필 이미지, userId, 닉네임, 미읽음 알림 갯수 반환 (nav바용)
     */
    @GetMapping("/me")
    @Operation(summary = "nav바 유저 정보 조회(프로필 사진, userId, 유저 닉네임, 알림 갯수)", description = "유저의 정보를 조회합니다. 프로필 이미지와 userId, 알림 갯수 반환")
    public ApiResponse<?> getUserProfileInfo() {
        Long userId = JwtTokenExtractor.getUserId();
        return ApiResponse.ok(userService.getUserInfo(userId), "조회되었습니다.");
    }

    /**
     * [PATCH] /api/v1/users/profile
     * 유저 프로필 업데이트 - bio, 프로필 이미지 변경
     */
    @PatchMapping("/profile")
    @Operation(summary = "유저 프로필 업데이트 (프로필 사진, bio)")
    public ResponseEntity<ApiResponse<?>> updateUserProfile(
            @Valid @RequestParam(value = "bio") UserProfileUpdateRequestDto request,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
        Long userId = JwtTokenExtractor.getUserId();
        userService.updateUserProfile(userId, request, profileImage);
        return ResponseEntity.ok(ApiResponse.ok("성공적으로 유저의 프로필을 업데이트 했습니다."));
    }

    /**
     * [DELETE] /api/v1/users/me
     * 회원 탈퇴 - 계정 및 관련 데이터 모두 삭제, refreshToken 무효화
     */
    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "계정과 관련된 모든 데이터를 삭제합니다.")
    public ResponseEntity<ApiResponse<?>> deleteUser() {
        Long userId = JwtTokenExtractor.getUserId();
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("성공적으로 계정을 삭제하였습니다."));
    }
}
