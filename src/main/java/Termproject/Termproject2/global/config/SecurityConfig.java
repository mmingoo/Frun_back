package Termproject.Termproject2.global.config;

import Termproject.Termproject2.global.jwt.JWTFilter;
import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import Termproject.Termproject2.global.oauth2.service.CustomOAuth2UserService;
import Termproject.Termproject2.global.oauth2.service.CustomSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {

        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()));
        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil, refreshTokenService), UsernamePasswordAuthenticationFilter.class);

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );

        http
                .authorizeHttpRequests((auth) -> auth
                        // 인증 없이 접근 가능한 공개 경로
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // OAuth2 로그인 관련 (Spring Security 내부 경로)
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()

                        // 토큰 재발급, 로그아웃은 별도 처리
                        .requestMatchers("/api/v1/auth/reissue", "/api/v1/auth/logout").permitAll() // refreshToken 쿠키로 처리하므로 허용

                        // 정적 이미지 파일 공개 접근 허용
                        .requestMatchers("/images/**").permitAll()

                        // 비활성화 계정 활성화 - 토큰 없는 상태이므로 허용
                        // 단, 별도 보안 처리 필요
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/inactive/token").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/activate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/inactive-info").permitAll()


                        // 나머지 모든 API는 인증 필요
                        .anyRequest().authenticated()
                );
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 인증 실패 시 OAuth2 리다이렉트 대신 401 반환 (AJAX 요청 CORS 에러 방지)
        http
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);   // 쿠키 허용 (필수)
        config.setExposedHeaders(List.of("Authorization"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}