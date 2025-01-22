package com.keunsori.keunsoriserver.global.config;

import com.keunsori.keunsoriserver.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
       http
        .csrf(csrf -> csrf.disable())  // CSRF 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 사용 안함
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 로그인,회원가입은 가능.
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/signup").permitAll()

                        // 예약 관련된 건 일반 권한 필요
                        .requestMatchers("/reservation/**").hasAuthority("일반")

                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated())

               // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
