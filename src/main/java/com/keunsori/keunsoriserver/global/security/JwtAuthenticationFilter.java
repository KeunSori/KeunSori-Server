package com.keunsori.keunsoriserver.global.security;

import com.keunsori.keunsoriserver.domain.auth.login.JwtTokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenManager jwtTokenManager;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path=request.getServletPath();
        return path.startsWith("/auth/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String authenticatedToken = request.getHeader(jwtTokenManager.getHeader());
        String token = null;

        //Bearer 제거해서 토큰만 추출
        if(authenticatedToken != null && authenticatedToken.startsWith(jwtTokenManager.getPrefix())){
            token = authenticatedToken.substring(jwtTokenManager.getPrefix().length());
        }

        if(token != null && jwtTokenManager.validateToken(token)){
            String studentId = jwtTokenManager.getStudentIdFromToken(token);
            String status = jwtTokenManager.getStatusFromToken(token);
            System.out.println(status);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(studentId,null, List.of(()->status));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request,response);
    }

}
