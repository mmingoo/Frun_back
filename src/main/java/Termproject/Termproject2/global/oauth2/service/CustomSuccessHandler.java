package Termproject.Termproject2.global.oauth2.service;

import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 소셜 로그인 인증 성공 시 실행되는 핸들러
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException, IOException {

        // 1. 인증된 사용자 정보 꺼내기
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getUsername();

        // 2. 인증된 사용자의 권한(Role) 꺼내기
        //    authorities는 컬렉션이므로 iterator로 첫 번째 권한을 가져옴
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 3. username + role 기반으로 JWT 토큰 생성 (만료시간: 60초 * 5 =  5분)
        // 해킹 방어 용도를 위해 액세스 토큰은 5분, 리프레쉬 토큰은 7일
        String accessToken = jwtUtil.createJwt(username, role, 60 * 5L);

        // 4. 생성된 JWT를 쿠키에 담아 응답에 추가
        response.addCookie(createCookie("Authorization", accessToken));

        // 5. 로그인 성공 후 프론트엔드로 리다이렉트
        response.sendRedirect("http://localhost:3000/");
    }

    // JWT 토큰을 담을 쿠키 생성
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 5);   // 쿠키 만료시간 (JWT와 동일하게 설정)
        // cookie.setSecure(true);         // HTTPS 환경에서만 전송 (운영 배포 시 활성화)
        cookie.setPath("/");              // 모든 경로에서 쿠키 사용 가능
        cookie.setHttpOnly(true);         // JS에서 쿠키 접근 차단 (XSS 방어)

        return cookie;
    }
}