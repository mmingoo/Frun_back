package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import Termproject.Termproject2.global.jwt.TokenPairDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueServiceImpl implements ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    //TODO: RTR 방식으로 accessToken 발급 및 refreshToken 갱신
    @Override
    public TokenPairDto reissue(String refreshToken) {

        // 1. refreshToken 존재 여부 확인
        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        // 2. 만료 여부 확인
        if (jwtUtil.isExpired(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 3. category 확인
        if (!jwtUtil.getCategory(refreshToken).equals("refresh")) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN_CATEGORY);
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 4. Redis에 저장된 refreshToken과 비교
        if (!refreshTokenService.isValid(userId, refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 5. RTR: accessToken 발급 + refreshToken 재발급
        String newAccessToken = jwtUtil.createJwt("access", userId, username, role, 60 * 15 * 1000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", userId, username, role, 60 * 60 * 24 * 14 * 1000L);

        // 6. Redis 갱신
        refreshTokenService.delete(userId);
        refreshTokenService.save(userId, newRefreshToken);

        return new TokenPairDto(newAccessToken, newRefreshToken);
    }
}
