package com.keunsori.keunsoriserver.global.security;

import com.keunsori.keunsoriserver.domain.auth.service.RefreshTokenService;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.AuthException;
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
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path=request.getServletPath();
        return path.startsWith("/auth/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String accessToken = CookieUtil.getCookieValue(request, "Access-Token");
        String refreshToken = CookieUtil.getCookieValue(request, "Refresh-Token");

        if (accessToken == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7); // "Bearer " 뒷부분만 추출
            }
        }


        try{
            if (accessToken != null) {
                tokenUtil.validateToken(accessToken);

                String studentId = tokenUtil.getStudentIdFromToken(accessToken);
                String status = tokenUtil.getStatusFromToken(accessToken);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(studentId, null, List.of(()->status));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else if (refreshToken != null) {
                String studentId = tokenUtil.getStudentIdFromToken(refreshToken);
                String storedRefreshToken = refreshTokenService.getRefreshToken(studentId);

                if(storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)){
                    throw new AuthException(INVALID_REFRESH_TOKEN);
                }

                Member member = memberRepository.findByStudentIdIgnoreCase(studentId)
                        .orElseThrow(()->new AuthException(STUDENT_ID_NOT_EXISTS));

                String newAccessToekn = tokenUtil.generateAccessToken(member.getStudentId(), member.getName(), member.getStatus());
                CookieUtil.addAccessTokenCookie(response, newAccessToekn);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(studentId, null, List.of(new SimpleGrantedAuthority(member.getStatus().name())));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }catch (Exception e){
            //인증 실패 시 보안 컨텍스트 초기화
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);

    }

}
