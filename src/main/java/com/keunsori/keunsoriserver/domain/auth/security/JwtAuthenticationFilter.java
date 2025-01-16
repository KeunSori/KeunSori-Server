package com.keunsori.keunsoriserver.domain.auth.security;

import com.keunsori.keunsoriserver.domain.auth.login.JwtTokenCreater;
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
    private final JwtTokenCreater jwtTokenCreater;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String authedtoken=request.getHeader("Authorization");
        String token= null;

        //Bearer 제거해서 토큰만 추출
        if(authedtoken!=null && authedtoken.startsWith("Bearer ")){
            token=authedtoken.substring(7);
        }

        if(token!=null && jwtTokenCreater.validateToken(token)){
            String studentId=jwtTokenCreater.getStudentIdFromToken(token);
            String status=jwtTokenCreater.getStatusFromToken(token);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(studentId,null, List.of(()->status));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request,response);
    }

}
