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

    // 블랙리스트 등록 (계정 비활성화 시 액세스 토큰 즉시 차단)
    // TTL = 액세스 토큰 만료 시간(15분)과 동일하게 설정
    public void addToBlacklist(Long userId) {
        redisTemplate.opsForValue()
                .set("blacklist:" + userId, "inactive", 15, TimeUnit.MINUTES);
    }

    // 블랙리스트 여부 확인
    public boolean isBlacklisted(Long userId) {
        return redisTemplate.hasKey("blacklist:" + userId);
    }

    // 비활성화 코드 저장 (UUID → userId, TTL 5분)
    public void saveInactiveCode(String code, Long userId) {
        redisTemplate.opsForValue()
                .set("inactive:" + code, String.valueOf(userId), 5, TimeUnit.MINUTES);
    }

    // 비활성화 코드 조회 및 즉시 삭제 (일회성 보장)
    public Long getAndDeleteInactiveCode(String code) {
        String key = "inactive:" + code;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;
        redisTemplate.delete(key);
        return Long.valueOf(value);
    }
}
