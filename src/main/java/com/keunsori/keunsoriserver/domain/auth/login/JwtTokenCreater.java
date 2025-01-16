package com.keunsori.keunsoriserver.domain.auth.login;

import com.keunsori.keunsoriserver.domain.member.MemberStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenCreater {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long accessTokenValidity = 30 * 60 * 1000L;         // AccessToken 만료시간: 30분
    private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000L; // RefreshToken 만료시간: 1주일

    // Access Token 생성
    public String generateAccessToken(String studentId, String name, MemberStatus status) {
        return createToken(studentId, name, status, accessTokenValidity);
    }

    // Refresh Token 생성
    public String generateRefreshToken(String studentId, String name, MemberStatus status) {
        return createToken(studentId, name, status, refreshTokenValidity);
    }

    // 토큰 생성 로직
    private String createToken(String studentId, String name, MemberStatus status, long validityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(studentId);
        claims.put("name", name);
        claims.put("status", status.toString());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        claims.put("expiration", validity.getTime());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰 만료 시간 조회
    public Long getExpirationTime(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }

    // 학번(StudentId) 조회
    public String getStudentIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 상태(Status) 조회
    public String getStatusFromToken(String token) {
        return (String) Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("status");
    }
}
