package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LogoutController {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenExtractor jwtTokenExtractor;

    @PostMapping("/logout")
    public ApiResponse<?> logout(HttpServletResponse response) {

        // 1. SecurityContext에서 userId 추출
        Long userId = jwtTokenExtractor.getUserId();

        // 2. Redis에서 refreshToken 삭제
        refreshTokenService.delete(userId);

        // 3. HttpOnly 쿠키 만료 처리 (maxAge=0으로 즉시 삭제)
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