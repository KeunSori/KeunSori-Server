package com.keunsori.keunsoriserver.global.config;

import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.DEV_URL;
import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.DEV_URL2;
import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.LOCAL_URL_1;
import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.LOCAL_URL_2;
import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.LOCAL_URL_3;
import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.PROD_URL;
import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.PROD_URL2;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.security.config.Customizer.withDefaults;

import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.global.security.JwtAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
       http
               .cors(withDefaults())
               .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화
               .sessionManagement(session -> session
                       .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 사용 안함
               .authorizeHttpRequests(auth -> auth
                       // 인증 없이 로그인,회원가입은 가능.
                       .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                       .requestMatchers("/auth/**").permitAll()
                       .requestMatchers("/signup").permitAll()

                       // 예약 관련된 건 일반 혹은 관리자 권한 필요
                       .requestMatchers("/reservation/**").hasAnyAuthority("일반", "관리자")

                       // 관리 기능은 관리자 권한 필요
                       .requestMatchers("/admin/**").hasAuthority("관리자")

                       // 나머지 요청은 인증 필요
                       .anyRequest().authenticated())

               // JWT 필터 추가
               .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of(
            LOCAL_URL_1, LOCAL_URL_2, LOCAL_URL_3, DEV_URL, DEV_URL2, PROD_URL, PROD_URL2
        ));

        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
//        configuration.addExposedHeader(SET_COOKIE);
        configuration.addExposedHeader("Refresh-Token");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
