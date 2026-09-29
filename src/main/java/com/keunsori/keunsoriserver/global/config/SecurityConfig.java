package com.keunsori.keunsoriserver.global.config;

import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.*;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.security.config.Customizer.withDefaults;

import com.keunsori.keunsoriserver.global.security.JwtAuthenticationFilter;

import java.util.List;

import com.keunsori.keunsoriserver.global.security.JwtExceptionFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {

    //비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //보안 설정(JWT 인증 및 권한)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, JwtExceptionFilter jwtExceptionFilter) throws Exception {
       http
               .cors(withDefaults())
               .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화
               .sessionManagement(session -> session
                       .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 사용 안함
               .authorizeHttpRequests(auth -> auth
                       // 로그인 여부 반환은 인증 필요
                       .requestMatchers("/auth/me").authenticated()

                       // 인증 없이 로그인,회원가입은 가능.
                       .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                       .requestMatchers("/auth/**", "/email/**").permitAll()
                       .requestMatchers("/signup").permitAll()

                       // 회원 관련된 건 일반 권한 필요
                       .requestMatchers("/members/**").hasAuthority("일반")

                       // 예약 관련된 건 일반 혹은 관리자 권한 필요
                       .requestMatchers("/reservation/**").hasAnyAuthority("일반", "관리자")

                       // 관리 기능은 관리자 권한 필요
                       .requestMatchers("/admin/**").hasAuthority("관리자")

                       // 나머지 요청은 인증 필요
                       .anyRequest().authenticated())
               .exceptionHandling(exception -> exception
                       .authenticationEntryPoint((request, response, authException) -> {
                           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                           response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                           response.setCharacterEncoding("UTF-8");
                           response.getWriter().write("{\"message\": \"인증에 실패하였습니다.\"}");
                       })
               )
               // JWT 필터 추가
               .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
               .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of(
                LOCAL_SERVER_URL, LOCAL_URL_1, LOCAL_URL_2, LOCAL_URL_3, LOCAL_URL_4,
                DEV_SERVER_URL, DEV_URL_1, DEV_URL_2, DEV_URL_3,
                PROD_SERVER_URL, PROD_URL_1, PROD_URL_2, PROD_URL_3
        ));

        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader(SET_COOKIE);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
