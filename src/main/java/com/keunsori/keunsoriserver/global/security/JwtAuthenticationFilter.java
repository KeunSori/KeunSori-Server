package com.keunsori.keunsoriserver.global.security;

import com.keunsori.keunsoriserver.domain.auth.repository.RefreshTokenRepository;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.AuthException;
import com.keunsori.keunsoriserver.global.properties.JwtProperties;
import com.keunsori.keunsoriserver.global.util.TokenUtil;
import com.keunsori.keunsoriserver.global.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_REFRESH_TOKEN;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.STUDENT_ID_NOT_EXISTS;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenUtil tokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 프리플라이트는 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getServletPath();
        return path.equals("/auth/login") || path.startsWith("/signup") || path.startsWith("/email/")
                || path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String accessToken = CookieUtil.getCookieValue(request, "Access-Token");
        String refreshToken = CookieUtil.getCookieValue(request, "Refresh-Token");

        // Swagger 전용
        String bearerAccessToken = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            bearerAccessToken = authHeader.substring(7);
        }

        boolean authenticated = false;

        // Access Token 검사
        if (accessToken != null){
            try {
                tokenUtil.validateToken(accessToken);

                String studentId = tokenUtil.getStudentIdFromToken(accessToken);
                String status = tokenUtil.getStatusFromToken(accessToken);

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                studentId,
                                null,
                                List.of(new SimpleGrantedAuthority(status))
                        )
                );
                authenticated = true;

                // Refresh-Token 만료 시 재발급
                boolean neednewRefreshToken = (refreshToken == null);

                if (!neednewRefreshToken) {
                    try {
                        tokenUtil.validateToken(refreshToken);
                    } catch (AuthException e) {
                        neednewRefreshToken = true;
                    }
                }

                if (neednewRefreshToken) {
                    Member member = memberRepository.findByStudentIdIgnoreCase(studentId)
                            .orElseThrow(() -> new AuthException(STUDENT_ID_NOT_EXISTS));
                    String newRefreshToken = tokenUtil.generateRefreshToken(member.getStudentId(), member.getName(), member.getStatus());
                    refreshTokenRepository.saveRefreshToken(member.getStudentId(), newRefreshToken, JwtProperties.REFRESH_TOKEN_VALIDITY_TIME);
                    CookieUtil.addRefreshTokenCookie(response,newRefreshToken);
                }
            } catch (AuthException ignored) {
                // Access-Token 존재 X -> Refresh-Token 검사로 넘어감
            }
        }

        // Refresh Token 검사
        if (!authenticated && refreshToken != null) {
            tokenUtil.validateToken(refreshToken);

            String studentId = tokenUtil.getStudentIdFromToken(refreshToken);
            String storedRefreshToken = refreshTokenRepository.getRefreshToken(studentId);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                CookieUtil.deleteCookie(response, "Access-Token");
                CookieUtil.deleteCookie(response, "Refresh-Token");
                throw new AuthException(INVALID_REFRESH_TOKEN);
            }

            Member member = memberRepository.findByStudentIdIgnoreCase(studentId)
                    .orElseThrow(() -> new AuthException(STUDENT_ID_NOT_EXISTS));

            String newAccessToken = tokenUtil.generateAccessToken(member.getStudentId(), member.getName(), member.getStatus());

            CookieUtil.addAccessTokenCookie(response, newAccessToken);

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    studentId,
                    null,
                    List.of(new SimpleGrantedAuthority(member.getStatus().name()))
                    )
            );
            authenticated = true;
        }

        // Swagger 전용 다시 시도
        if (!authenticated && bearerAccessToken != null) {
            tokenUtil.validateToken(bearerAccessToken);

            String studentId = tokenUtil.getStudentIdFromToken(bearerAccessToken);
            String status = tokenUtil.getStatusFromToken(bearerAccessToken);

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            studentId,
                            null,
                            List.of(new SimpleGrantedAuthority(status))
                    )
            );
            authenticated = true;
        }

        chain.doFilter(request, response);

    }
}
