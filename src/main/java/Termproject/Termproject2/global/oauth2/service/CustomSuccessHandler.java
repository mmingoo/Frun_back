package Termproject.Termproject2.global.oauth2.service;

import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import Termproject.Termproject2.global.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        Long userId = customUserDetails.getUserId();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // 비활성화 계정인 경우 임시 토큰 발급하여 전달
        if (customUserDetails.isInactive()) {

            // 비활성화 계정인 경우 임시토큰 발급
            String tempToken = jwtUtil.createJwt("temp", userId, username, role, 5 * 60 * 1000L);

            // http://localhost:5173/inactive 로 리다이렉트
            getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/inactive?token=" + tempToken);
            System.out.println("리다이렉트 완료");
            return;
        }

        // refreshToken 발급 후 Redis + HttpOnly 쿠키에 저장
        String refreshToken = jwtUtil.createJwt("refresh", userId, username, role, 60 * 60 * 24 * 14 * 1000L);
        refreshTokenService.save(userId, refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)        // 운영 시 true (HTTPS)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // accessToken은 URL에 포함하지 않음 — 프론트가 /api/v1/auth/reissue로 발급받음
        if (customUserDetails.isNewUser()) {
            getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/signup/nickname");
        } else {
            getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/feed");
        }
    }
}