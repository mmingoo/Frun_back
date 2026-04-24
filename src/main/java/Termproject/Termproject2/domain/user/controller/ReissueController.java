package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.domain.user.service.ReissueService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.TokenPairDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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

    private final ReissueService reissueService;

    /**
     * [POST] /api/v1/auth/reissue
     * 토큰 재발급 - RTR 방식으로 accessToken 발급 및 refreshToken 갱신
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        // 1. 토큰 유효성 검증 및 재발급
        TokenPairDto tokenPair = reissueService.reissue(refreshToken);

        // 2. refreshToken → HttpOnly 쿠키 갱신
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenPair.refreshToken())
                .httpOnly(true)
                .secure(false)        // 운영 시 true (HTTPS)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        System.out.println("토큰 재발급 성공");
        // 3. accessToken → JSON body 반환
        return ResponseEntity.ok(ApiResponse.ok(
                Map.of("accessToken", tokenPair.accessToken()),
                "토큰이 재발급되었습니다."
        ));
    }
}
