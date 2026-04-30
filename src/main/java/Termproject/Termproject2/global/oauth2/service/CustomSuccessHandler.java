package Termproject.Termproject2.global.oauth2.service;

import Termproject.Termproject2.global.jwt.RefreshTokenCookieService;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import Termproject.Termproject2.global.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

// 토근 발급
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenCookieService refreshTokenCookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // 로그인 성공한 유저 정보 가져오기
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        Long userId = customUserDetails.getUserId();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // 비활성화 계정인 경우 UUID 코드를 Redis에 저장 후 프론트에 전달, 종료
        if (customUserDetails.isInactive()) {
            handleInactiveUser(request, response, userId);
            return;
        }

        // refreshToken 발급 후 Redis + HttpOnly 쿠키에 저장
        refreshTokenCookieService.issueRefreshTokenCookie(userId, username, role, response);

        // accessToken은 URL에 포함하지 않음 — 프론트가 /api/v1/auth/reissue로 발급받음
        redirectByUserStatus(request, response, customUserDetails);
    }

    // 비활성화 계정인 경우 UUID 코드를 Redis에 저장 후 프론트에 전달, 종료
    private void handleInactiveUser(HttpServletRequest request, HttpServletResponse response, Long userId) throws IOException {
        //UUID 발급
        String code = UUID.randomUUID().toString();

        // redis에 UUID 발급 내역 저장
        refreshTokenService.saveInactiveCode(code, userId);

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/inactive?code=" + code);
    }

    private void redirectByUserStatus(HttpServletRequest request, HttpServletResponse response, CustomOAuth2User customUserDetails) throws IOException {
        if (customUserDetails.isNewUser()) {
            getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/signup/nickname");
        } else {
            getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/feed");
        }
    }
}