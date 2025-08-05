package org.biz.shopverse.config;

import org.biz.shopverse.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // roles 사용을 위해 추가
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint entryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1) CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // 2) CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 3) 세션을 사용하지 않음(STATELESS)
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4) 인증 실패 시 401 에러 처리 엔트리 포인트
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint))

                // 5) 요청별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/member/**", "/docs/**").permitAll()   // /auth/** 은 모두 허용
                        .anyRequest().authenticated()              // 그 외 인증 필요
                )

                // 6) JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 origin 설정
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 모든 origin 허용 (개발 환경)
        // 또는 특정 origin만 허용: Arrays.asList("http://localhost:3000", "http://localhost:3001")
        
        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 인증 정보 허용 (JWT 토큰 전송을 위해)
        configuration.setAllowCredentials(true);
        
        // preflight 요청의 캐시 시간 설정 (초 단위)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}