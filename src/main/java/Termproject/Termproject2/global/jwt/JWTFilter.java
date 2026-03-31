package Termproject.Termproject2.global.jwt;

import Termproject.Termproject2.domain.user.entity.Role;
import Termproject.Termproject2.global.oauth2.dto.CustomOAuth2User;
import Termproject.Termproject2.global.oauth2.dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // ① Authorization 헤더에서 Bearer 토큰 추출
        String token = null;
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        System.out.println("▶ [JWT] accessToken: " + token);

        // ② 토큰이 없으면 인증 처리 없이 다음 필터로 통과 (비로그인 사용자)
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 만료됐으면 인증 처리 없이 다음 필터로 통과
        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // access 토큰인지 확인 (refresh 토큰으로 인증 시도 차단)
        if (!jwtUtil.getCategory(token).equals("access")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 Payload에서 유저 정보 추출 (DB 조회 없이 토큰만으로 처리)
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // 추출한 정보로 UserDTO 및 CustomOAuth2User 객체 생성
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setUsername(username);
        userDTO.setRole(Role.valueOf(role));
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        // Authentication 객체 생성 (credentials는 JWT 방식이므로 null)
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customOAuth2User, null, customOAuth2User.getAuthorities()
        );

        // SecurityContext에 인증 정보 등록 → 이 요청은 인증된 사용자로 처리됨
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 인증 완료 후 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}