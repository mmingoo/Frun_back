package Termproject.Termproject2.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 템플릿 객체 생성
        RedisTemplate<String, String> template = new RedisTemplate<>();

        // application.yml에 설정한 Redis 서버(host, port)와 연결
        template.setConnectionFactory(redisConnectionFactory);

        // key 를 직렬화할 때 String 형태로 저장
        template.setKeySerializer(new StringRedisSerializer());

        //value를 직렬화할 때 String 형태로 저장
        template.setValueSerializer(new StringRedisSerializer());

        return template;

    }
}
