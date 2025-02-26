package com.keunsori.keunsoriserver.domain.auth.service;

import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.global.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    // Access Token 생성
    public String generateAccessToken(String studentId, String name, MemberStatus status) {
        return createToken(studentId, name, status, jwtProperties.ACCESS_TOKEN_VALIDITY_TIME);
    }

    // Refresh Token 생성 (Redis에 저장 포함)
    public String generateRefreshToken(String studentId, String name, MemberStatus status) {
        String refreshToken = createToken(studentId, name, status, jwtProperties.REFRESH_TOKEN_VALIDITY_TIME);
        refreshTokenService.saveRefreshToken(studentId, refreshToken, jwtProperties.REFRESH_TOKEN_VALIDITY_TIME);
        return refreshToken;
    }

    // JWT 토큰 생성 로직
    private String createToken(String studentId, String name, MemberStatus status, long validity) {
        Claims claims = Jwts.claims().setSubject(studentId);
        claims.put("name", name);
        claims.put("status", status.toString());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            token = removePrefix(token);  // Bearer 접두어 제거 후 검증
            Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false; // 토큰 만료
        } catch (Exception e) {
            return false; // 유효하지 않은 토큰
        }
    }

    // 학번(StudentId) 조회
    public String getStudentIdFromToken(String token) {
        token = removePrefix(token);  // Bearer 접두어 제거
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 상태(Status) 조회
    public String getStatusFromToken(String token) {
        token = removePrefix(token);  // Bearer 접두어 제거
        return (String) Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .get("status");
    }

    // 토큰 만료 시간 조회
    public Long getExpirationTime(String token) {
        token = removePrefix(token);  // Bearer 접두어 제거
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }

    // Refresh Token 삭제 (로그아웃 시 사용)
    public void removeRefreshToken(String studentId) {
        refreshTokenService.deleteRefreshToken(studentId);
    }

    // JWT 인증 헤더 반환
    public String getHeader() {
        return jwtProperties.HEADER;
    }

    // JWT Prefix 반환
    public String getPrefix() {
        return jwtProperties.PREFIX;
    }

    // Bearer 접두어 제거
    private String removePrefix(String token) {
        String prefix = jwtProperties.PREFIX;
        if (token != null && token.startsWith(prefix + " ")) {
            return token.substring(prefix.length() + 1);
        }
        return token;
    }
}
