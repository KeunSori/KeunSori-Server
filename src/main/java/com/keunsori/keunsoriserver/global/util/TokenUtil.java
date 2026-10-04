package com.keunsori.keunsoriserver.global.util;

import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.global.exception.AuthException;
import com.keunsori.keunsoriserver.global.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;


import static com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus.일반;
import static com.keunsori.keunsoriserver.global.constant.TokenConstant.PASSWORD_UPDATE_TOKEN_VALIDITY_TIME;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.EXPIRED_TOKEN;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_TOKEN;

@Component
@RequiredArgsConstructor
public class TokenUtil {

    private final JwtProperties jwtProperties;

    // Access Token 생성
    public String generateAccessToken(String studentId, String name, MemberStatus status) {
        return createToken(studentId, name, status, JwtProperties.ACCESS_TOKEN_VALIDITY_TIME);
    }

    // Refresh Token 생성
    public String generateRefreshToken(String studentId, String name, MemberStatus status) {
        return createToken(studentId, name, status, JwtProperties.REFRESH_TOKEN_VALIDITY_TIME);
    }

    public String generatePasswordUpdateToken(String studentId) {
        return createToken(studentId, "", 일반, PASSWORD_UPDATE_TOKEN_VALIDITY_TIME);
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
    public void validateToken(String token) {
        try {
            token = removePrefix(token);  // Bearer 접두어 제거 후 검증
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecret())
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
           throw new AuthException(EXPIRED_TOKEN); // 토큰 만료
        } catch (Exception e) {
            throw new AuthException(INVALID_TOKEN); // 유효하지 않은 토큰
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

    // Swagger에서 테스트할 경우 Bearer 접두어 제거
    private String removePrefix(String token) {
        String prefix = JwtProperties.PREFIX;
        if (token != null && token.startsWith(prefix + " ")) {
            return token.substring(prefix.length() + 1);
        }
        return token;
    }
}
