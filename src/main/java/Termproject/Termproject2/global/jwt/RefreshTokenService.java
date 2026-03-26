package Termproject.Termproject2.global.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRE = 60 * 60 * 24 * 15L;

    // 레디스에 refreshToken 저장
    // refresh:1 , `"eyJhbGci..." 형태로 레디스에 저장
    public void save(Long userId, String refreshToken){
        redisTemplate.opsForValue()
                .set("refresh:" + userId, refreshToken,REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);
    }

    // userId 의 refreshToken 조회
    public String get(Long userId){
        return redisTemplate.opsForValue().get("refresh:" + userId);
    }

    // refreshToken 삭제
    public void delete(Long userId){
        redisTemplate.delete("refresh:" + userId);
    }

    // refreshToken 유효성 검증
    public boolean isValid(Long userId, String refreshToken){
        String saved = get(userId);
        return saved != null && saved.equals(refreshToken);
    }
}
