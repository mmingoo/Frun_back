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

        Long userId = jwtUtil.getUserId(refreshToken);
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 1. refreshToken 존재 여부 확인
        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        // 2. 만료 여부 확인
        if (jwtUtil.isExpired(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 3. category(토큰의 종류) 확인
        if (!jwtUtil.getCategory(refreshToken).equals("refresh")) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN_CATEGORY);
        }

        // 4. Redis에 저장된 refreshToken과 비교
        String savedToken = refreshTokenService.get(userId);

        // refresh token 이 null 이거나 , 현재 refresh 와 redis 에 있는 refresh 가 일치하지 않는 경우
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            System.out.println("▶ [Reissue] FAIL reason : " + (savedToken == null ? "Redis에 토큰 없음" : "토큰 불일치"));
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 5. RTR: accessToken 발급 + refreshToken 재발급
        String newAccessToken = jwtUtil.createJwt("access", userId, username, role, 60 * 15 * 1000L); // 15분
        String newRefreshToken = jwtUtil.createJwt("refresh", userId, username, role, 60 * 60 * 24 * 14 * 1000L); // 일주일

        // 6. Redis 갱신 (save는 원자적 덮어쓰기 — delete 불필요)
        // redis 엔 refresh 만 저장하고, accessToken → JSON body 반환
        refreshTokenService.save(userId, newRefreshToken);

        return new TokenPairDto(newAccessToken, newRefreshToken);
    }
}
