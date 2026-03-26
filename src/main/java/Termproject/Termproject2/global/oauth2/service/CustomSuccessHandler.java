package Termproject.Termproject2.global.oauth2.service;

import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import Termproject.Termproject2.global.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // 유저 정보 조회
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        Long userId = customUserDetails.getUserId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.iterator().next().getAuthority();

        // accessToken 15분, refreshToken 15일 생성
        String accessToken = jwtUtil.createJwt("access", userId, username, role, 60 * 15 * 1000L);
        String refreshToken = jwtUtil.createJwt("refresh", userId, username, role, 60 * 60 * 24 * 15 * 1000L);

        // Redis에 refreshToken 저장
        refreshTokenService.save(userId, refreshToken);

        // 쿠키에 담기
        response.addCookie(createCookie("Authorization", accessToken, 60 * 15));
        response.addCookie(createCookie("RefreshToken", refreshToken, 60 * 60 * 24 * 15));

        if (customUserDetails.isNewUser()) {
            response.sendRedirect("http://localhost:5173/signup/nickname");
        } else {
            response.sendRedirect("http://localhost:5173/feed");
        }
    }

    private Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}