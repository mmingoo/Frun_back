package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.service.UserService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import Termproject.Termproject2.global.jwt.RefreshTokenCookieService;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Account", description = "계정 활성화/비활성화 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenCookieService refreshTokenCookieService;

    /**
     * [POST] /api/v1/users/inactive/token
     * 임시 토큰 발급 - 비활성화 코드(UUID)를 5분 유효 임시 토큰으로 교환
     */
    @PostMapping("/inactive/token")
    @Operation(summary = "임시 토큰 발급", description = "비활성화 코드(UUID)를 임시 토큰으로 교환합니다. 코드는 일회성이며 5분간 유효합니다.")
    public ResponseEntity<ApiResponse<?>> exchangeInactiveToken(@RequestParam String code) {
        // userId 조회
        Long userId = refreshTokenService.getAndDeleteInactiveCode(code);

        // user 조회
        User user = userService.findUserById(userId);

        // 임시 토큰 생성
        String tempToken = jwtUtil.createJwt("temp", userId, user.getUserName(), user.getRole().toString(), 5 * 60 * 1000L);
        return ResponseEntity.ok(ApiResponse.ok(tempToken, "임시 토큰이 발급되었습니다."));
    }

    /**
     * [GET] /api/v1/users/inactive-info
     * 비활성화 계정 정보 조회 - 임시 토큰 필요, 비활성화 날짜·삭제 예정일 반환
     */
    @GetMapping("/inactive-info")
    @Operation(summary = "비활성화 계정 정보 조회", description = "임시 토큰으로 비활성화 날짜와 삭제 예정일을 반환합니다.")
    public ApiResponse<?> getInactiveInfo(@RequestHeader("Authorization") String bearerToken) {
        Long userId = extractUserIdFromTempToken(bearerToken);
        return ApiResponse.ok(userService.getInactiveInfo(userId), "비활성화 계정 정보를 조회했습니다.");
    }

    /**
     * [PATCH] /api/v1/users/activate
     * 계정 활성화 - 임시 토큰으로 INACTIVE 계정을 ACTIVE 상태로 변경 후 refreshToken 쿠키 발급
     */
    @PatchMapping("/activate")
    @Operation(summary = "유저 활성화", description = "임시 토큰으로 비활성화된 계정을 활성화하고 refreshToken 쿠키를 발급합니다.")
    public ResponseEntity<ApiResponse<?>> userActivate(
            @RequestHeader("Authorization") String bearerToken,
            HttpServletResponse response) {

        // 임시토큰으로 userId 추출
        Long userId = extractUserIdFromTempToken(bearerToken);

        // 계정 활성화
        userService.userActivate(userId);

        // user 조회
        User user = userService.findUserById(userId);

        // refreshToken 발급 후 Redis + HttpOnly 쿠키에 저장
        refreshTokenCookieService.issueRefreshTokenCookie(userId, user.getUserName(), user.getRole().toString(), response);

        return ResponseEntity.ok(ApiResponse.ok(userId, "성공적으로 계정을 활성화하였습니다."));
    }

    /**
     * [DELETE] /api/v1/users/deactivate
     * 계정 비활성화 - INACTIVE 상태로 변경, refreshToken 즉시 무효화
     */
    @DeleteMapping("/deactivate")
    @Operation(summary = "유저 비활성화")
    public ResponseEntity<ApiResponse<?>> userDeactivate(HttpServletResponse response) {
        Long userId = JwtTokenExtractor.getUserId();
        Long result = userService.userDeactivate(userId);
        refreshTokenCookieService.clearRefreshTokenCookie(response);
        return ResponseEntity.ok(ApiResponse.ok(result, "성공적으로 계정을 비활성화하였습니다."));
    }

    // 토큰에서 id 추출
    private Long extractUserIdFromTempToken(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String token = bearerToken.substring(7);

        if (jwtUtil.isExpired(token) || !"temp".equals(jwtUtil.getCategory(token))) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return jwtUtil.getUserId(token);
    }
}
