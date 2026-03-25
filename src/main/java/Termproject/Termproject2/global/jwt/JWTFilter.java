package Termproject.Termproject2.global.jwt;

import Termproject.Termproject2.domain.user.entity.Role;
import Termproject.Termproject2.global.oauth2.dto.CustomOAuth2User;
import Termproject.Termproject2.global.oauth2.dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    // 요청마다 JWT 토큰을 검증하는 필터 (OncePerRequestFilter → 요청당 1번만 실행 보장)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Authorization 헤더에서 Bearer 토큰 먼저 확인 (Swagger 지원)
        String authorization = null;
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            authorization = bearerToken.substring(7);
        }

        // 2. 헤더에 없으면 쿠키에서 확인
        if (authorization == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("Authorization")) {
                        authorization = cookie.getValue();
                    }
                }
            }
        }

        // 3. 헤더, 쿠키 모두 없으면 인증 없이 다음 필터로 넘기고 종료
        if (authorization == null) {
            System.out.println("token null");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization;

        // 4. 토큰 만료 여부 검증 → 만료됐으면 다음 필터로 넘기고 종료
        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);
            return;
        }

        // 5. 토큰에서 userId, username, role 추출
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // 6. 추출한 정보로 UserDTO 생성
        //    매 요청마다 DB 조회 없이 토큰 정보만으로 인증 처리
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setUsername(username);
        userDTO.setRole(Role.valueOf(role));

        // 7. UserDTO를 담은 CustomOAuth2User 생성 (UserDetails 역할)
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        // 8. Spring Security 인증 토큰 생성 후 SecurityContext에 등록
        //    → 이후 컨트롤러에서 @AuthenticationPrincipal로 사용자 정보 접근 가능
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customOAuth2User, null, customOAuth2User.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 9. 인증 완료 → 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}