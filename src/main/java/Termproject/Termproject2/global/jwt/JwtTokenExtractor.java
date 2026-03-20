package Termproject.Termproject2.global.jwt;

import Termproject.Termproject2.global.oauth2.dto.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenExtractor {

    /**
     * 현재 요청의 SecurityContext에서 userId를 추출
     * JWTFilter에서 토큰 파싱 후 SecurityContext에 등록된 인증 정보를 사용
     *
     * @return 인증된 사용자의 userId
     * @throws IllegalStateException 인증 정보가 없거나 유효하지 않을 경우
     */
    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomOAuth2User)) {
            throw new IllegalStateException("유효하지 않은 인증 정보입니다.");
        }

        return ((CustomOAuth2User) principal).getUserId();
    }
}
