package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {


        // 1. 쿠키에서 refreshToken 꺼내기
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("RefreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("refresh token이 없습니다."));
        }

        // 2. 만료 여부 확인
        if (jwtUtil.isExpired(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("refresh token이 만료되었습니다."));
        }

        // 3. category 확인
        if (!jwtUtil.getCategory(refreshToken).equals("refresh")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("유효하지 않은 토큰입니다."));
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 4. Redis에 저장된 refreshToken과 비교
        if (!refreshTokenService.isValid(userId, refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("유효하지 않은 refresh token입니다."));
        }

        // 5. RTR: accessToken 발급 시 refreshToken 도 재발급
        String newAccessToken = jwtUtil.createJwt("access", userId, username, role, 60 * 15 * 1000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", userId, username, role, 60 * 60 * 24 * 15 * 1000L);

        // 6. Redis 기존 refreshToken 삭제 후 새 refreshToken 저장
        refreshTokenService.delete(userId);
        refreshTokenService.save(userId, newRefreshToken);

        // 7. 새 토큰 반환
        // refreshToken은 쿠키로, accessToken은 바디로 반환
        response.addCookie(createCookie("RefreshToken", newRefreshToken, 60 * 60 * 24 * 15));

        return ResponseEntity.ok(ApiResponse.ok(
                Map.of("accessToken", newAccessToken),
                "토큰이 재발급되었습니다."
        ));
    }

    private Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}