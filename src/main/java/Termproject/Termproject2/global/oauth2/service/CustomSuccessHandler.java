package Termproject.Termproject2.global.oauth2.service;

import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import Termproject.Termproject2.global.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

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

        // accessToken 15분, refreshToken 15일
        String accessToken = jwtUtil.createJwt("access", userId, username, role, 60 * 15 * 1000L);
        String refreshToken = jwtUtil.createJwt("refresh", userId, username, role, 60 * 60 * 24 * 15 * 1000L);

        // refreshToken 쿠키 + Redis에 저장
        refreshTokenService.save(userId, refreshToken);
        response.addHeader("Set-Cookie", createCookie("RefreshToken", refreshToken, 60 * 60 * 24 * 15).toString());

        // accessToken HttpOnly 쿠키로 전달 (15분)
        response.addHeader("Set-Cookie", createCookie("AccessToken", accessToken, 60 * 15).toString());

        if (customUserDetails.isNewUser()) {
            response.sendRedirect("http://localhost:5173/signup/nickname");
        } else {
            response.sendRedirect("http://localhost:5173/feed");
        }
    }

    private ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}