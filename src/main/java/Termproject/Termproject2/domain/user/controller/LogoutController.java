package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LogoutController {

    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;

    /**
     * [POST] /api/v1/auth/logout
     *
     * case1 - Access Token 유효: userId 추출 → refreshToken 삭제 + accessToken 블랙리스트 등록
     * case2 - Access Token 만료: refreshToken 쿠키로 userId 추출 → refreshToken 삭제만
     */
    @PostMapping("/logout")
    public ApiResponse<?> logout(
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        Long userId = null;
        String validAccessToken = null;

        // case1: 유효한 Access Token이 있는 경우
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            try {
                if (!jwtUtil.isExpired(accessToken) && "access".equals(jwtUtil.getCategory(accessToken))) {
                    userId = jwtUtil.getUserId(accessToken);
                    validAccessToken = accessToken;
                }
            } catch (Exception ignored) {
                // 만료된 토큰 → case2로 처리
            }
        }

        // case2: Access Token 만료/없음 → refreshToken으로 userId 추출
        if (userId == null) {
            if (refreshToken == null) {
                throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISSING);
            }
            try {
                userId = jwtUtil.getUserId(refreshToken);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
            }
        }

        // Redis에서 refreshToken 삭제
        refreshTokenService.delete(userId);

        // case1에서만 로그아웃 블랙리스트 등록 (토큰 잔여 TTL 동안만 유지)
        if (validAccessToken != null) {
            long remaining = jwtUtil.getRemainingTtlMillis(validAccessToken);
            refreshTokenService.addToLogoutBlacklist(validAccessToken, remaining);
        }

        // HttpOnly 쿠키 만료 처리
        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

        return ApiResponse.ok("로그아웃 되었습니다.");
    }
}
