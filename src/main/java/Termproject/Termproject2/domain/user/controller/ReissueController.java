package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        // 1. refreshToken 존재 여부 확인
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

        // 5. RTR: accessToken 발급 + refreshToken 재발급
        String newAccessToken = jwtUtil.createJwt("access", userId, username, role, 60 * 15 * 1000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", userId, username, role, 60 * 60 * 24 * 14 * 1000L);

        // 6. Redis 갱신
        refreshTokenService.delete(userId);
        refreshTokenService.save(userId, newRefreshToken);

        // 7. refreshToken → HttpOnly 쿠키, accessToken → JSON body
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false)        // 운영 시 true (HTTPS)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(ApiResponse.ok(
                Map.of("accessToken", newAccessToken),
                "토큰이 재발급되었습니다."
        ));
    }
}
