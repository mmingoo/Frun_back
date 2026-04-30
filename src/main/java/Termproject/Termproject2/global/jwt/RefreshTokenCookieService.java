package Termproject.Termproject2.global.jwt;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenCookieService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    // HttpOnly 쿠키에서 refreshToken 삭제 (만료 처리)
    public void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
    }

    // refreshToken 발급 후 Redis + HttpOnly 쿠키에 저장
    public void issueRefreshTokenCookie(Long userId, String username, String role, HttpServletResponse response) {
        String refreshToken = jwtUtil.createJwt("refresh", userId, username, role, 60 * 60 * 24 * 14 * 1000L);

        // refreshToken 저장
        refreshTokenService.save(userId, refreshToken);

        //HttpOnly 쿠키에 세팅
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)        // 운영 시 true (HTTPS)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
