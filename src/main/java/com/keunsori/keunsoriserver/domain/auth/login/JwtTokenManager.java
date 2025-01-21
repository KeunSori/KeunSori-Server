package com.keunsori.keunsoriserver.domain.auth.login;

import com.keunsori.keunsoriserver.domain.auth.redis.RefreshTokenService;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.global.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenManager {

    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    public JwtTokenManager( RefreshTokenService refreshTokenService,JwtProperties jwtProperties) {
        this.refreshTokenService = refreshTokenService;
        this.jwtProperties = jwtProperties;
    }


    public String getHeader(){
        return jwtProperties.HEADER;
    }

    public String getPrefix(){
        return jwtProperties.PREFIX;
    }

    // Access Token 생성
    public String generateAccessToken(String studentId, String name, MemberStatus status) {
        return createToken(studentId, name, status, jwtProperties.ACCESS_TOKEN_VALIDITY_TIME);
    }

    // Refresh Token 생성
    public String generateRefreshToken(String studentId, String name, MemberStatus status) {
        String refreshToken=createToken(studentId, name, status, jwtProperties.REFRESH_TOKEN_VALIDITY_TIME);
        refreshTokenService.saveRefreshToken(studentId, refreshToken, jwtProperties.REFRESH_TOKEN_VALIDITY_TIME);
        return refreshToken;
    }

    // 토큰 생성 로직
    private String createToken(String studentId, String name, MemberStatus status, long validityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(studentId);
        claims.put("name", name);
        claims.put("status", status.toString());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("토큰 만료");
            return false;
        }catch (Exception e) {
            System.out.println("유효하지 않은 토큰");
            return false;
        }
    }

    // 토큰 만료 시간 조회
    public Long getExpirationTime(String token) {
        token=removePrefix(token);
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }

    // 학번(StudentId) 조회
    public String getStudentIdFromToken(String token) {
        token=removePrefix(token);
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 상태(Status) 조회
    public String getStatusFromToken(String token) {
        token=removePrefix(token);
        return (String) Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .get("status");
    }

    // 로그아웃시 refresh token 삭제
    public void removeRefreshToken(String studentId) {
        refreshTokenService.deleteRefreshToken(studentId);
    }

    // 토큰에서 접두사 제거
    private String removePrefix(String token) {
        String prefix=jwtProperties.PREFIX;
        if(token.startsWith(prefix+" ")) {
            return token.substring(prefix.length()+1);
        }
        return token;
    }
}
