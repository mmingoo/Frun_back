package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;

    @Override
    public void logout(String bearerToken, String refreshToken) {
        // accessToken 추출
        String accessToken = extractValidAccessToken(bearerToken);

        // userId 추출
        Long userId = extractUserId(accessToken, refreshToken);

        // Redis에서 refreshToken 삭제
        refreshTokenService.delete(userId);

        // case1에서만 로그아웃 블랙리스트 등록 (토큰 잔여 TTL 동안만 유지)
        if (accessToken != null) {
            refreshTokenService.addToLogoutBlacklist(accessToken, jwtUtil.getRemainingTtlMillis(accessToken));
        }
    }

    // 유효한 Access Token 추출, 만료되거나 유효하지 않으면 null 반환 → case2로 처리
    private String extractValidAccessToken(String bearerToken) {
        // 토큰이 null 이거나 올바르지 않은 형식인 경우 종료
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null;
        }

        // Access token 추출
        String accessToken = bearerToken.substring(7);
        try {
            // AccessToken 이 만료되지 않았고, 토큰의 종료가 access 인 경우 토큰 반환
            if (!jwtUtil.isExpired(accessToken) && "access".equals(jwtUtil.getCategory(accessToken))) {
                return accessToken;
            }
        } catch (Exception ignored) {
            // 만료된 토큰 → case2로 처리
        }
        return null;
    }

    // userId 추출: Access Token 우선, 없으면 refreshToken에서
    private Long extractUserId(String accessToken, String refreshToken) {
        // 토큰 추출(Acceess 있으면 Access 반환, 없으면 refresh)
        String token = accessToken != null ? accessToken : refreshToken;

        // 토큰 없는 경우 에러 발생
        if (token == null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        // userId 추출
        try {
            return jwtUtil.getUserId(token);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
