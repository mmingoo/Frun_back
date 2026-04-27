package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.domain.user.service.LogoutService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LogoutController {

    private final LogoutService logoutService;

    /**
     * [POST] /api/v1/auth/logout
     *
     * case1 - Access Token 유효한 상태에서 로그아웃 시도: userId 추출 → refreshToken 삭제 + accessToken 블랙리스트 등록
     *       - Access Token이 탈취된 경우까지 고려하여 Redis accessToken 블랙리스트 등록
     *
     * case2 - Access Token 만료된 상황에서 로그아웃 시도: refreshToken 쿠키로 userId 추출 → refreshToken 삭제만
     */
    @PostMapping("/logout")
    public ApiResponse<?> logout(
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        logoutService.logout(bearerToken, refreshToken);

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
